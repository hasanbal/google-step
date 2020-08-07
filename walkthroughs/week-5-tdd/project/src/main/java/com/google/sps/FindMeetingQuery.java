// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class FindMeetingQuery {

  private static final int MINUTES_IN_ONE_DAY = 1440;

  public boolean isMinuteEmpty(int statusOfMinute) {
    if (statusOfMinute == 0) {
      return true;
    }
    return false;
  }

  public boolean isMandatoryFull(int statusOfMinute) {
    if (statusOfMinute == 1) {
      return true;
    }
    return false;
  }

  public List<TimeRange> findTimeRanges(
      int[] takenMinutes, long duration, boolean ignoreOptionalAttendee) {

    List<TimeRange> res = new ArrayList<>();
    for (int i = 0; i < MINUTES_IN_ONE_DAY; i++) {
      if (ignoreOptionalAttendee == false && isMinuteEmpty(takenMinutes[i])
          || ignoreOptionalAttendee == true && !isMandatoryFull(takenMinutes[i])) {

        int curStart = i;
        int curEnd = i;

        for (int j = i; j < MINUTES_IN_ONE_DAY; j++) {
          if (ignoreOptionalAttendee == false && !isMinuteEmpty(takenMinutes[j])
              || ignoreOptionalAttendee == true && isMandatoryFull(takenMinutes[j])) {

            i = j;
            break;
          }
          i = j;
          curEnd = j;
        }
        int curDuration = curEnd - curStart + 1;

        if (duration <= curDuration) {
          res.add(TimeRange.fromStartDuration(curStart, curDuration));
        }
      }
    }
    return res;
  }

  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    long duration = request.getDuration();

    // 0: not taken minutes
    // 1: taken by at least one mandatory attendee
    // 2: not taken by mandatory attendee but taken by at least one optional attendee
    int[] takenMinutes = new int[MINUTES_IN_ONE_DAY];

    List<TimeRange> res = new ArrayList<>();
    List<String> requestAttendees = new ArrayList(request.getAttendees());
    List<String> optionalAttendees = new ArrayList(request.getOptionalAttendees());

    for (Event event : events) {
      int rangeStart = event.getWhen().start();
      int rangeEnd = event.getWhen().end();

      List<String> eventAttendees = new ArrayList(event.getAttendees());

      boolean mustFill = false;
      boolean optionalFill = false;

      for (String attendee : eventAttendees) {
        if (requestAttendees.contains(attendee)) {
          mustFill = true;
        }
        if (optionalAttendees.contains(attendee)) {
          optionalFill = true;
        }
      }

      if (mustFill == true) {
        for (int i = rangeStart; i < rangeEnd; i++) {
          takenMinutes[i] = 1;
        }
      }

      if (mustFill == false && optionalFill == true) {
        // If there is any previous mustFill I'm not changing.
        // Otherwise I'm labeling with 2 because it's not preffered time.
        // Because there is optional attendee.
        for (int i = rangeStart; i < rangeEnd; i++) {
          if (takenMinutes[i] != 1) {
            takenMinutes[i] = 2;
          }
        }
      }
    }

    // First try to add request optional attendees.
    res = findTimeRanges(takenMinutes, duration, false);
    // Then if I can't find any timerange I will ignore optional attendees.
    if (res.isEmpty()) {
      res = findTimeRanges(takenMinutes, duration, true);
    }

    return res;
  }
}
