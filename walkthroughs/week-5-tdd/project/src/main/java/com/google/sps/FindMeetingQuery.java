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

  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    long duration = request.getDuration();
    int[] minutes = new int[MINUTES_IN_ONE_DAY];
    List<TimeRange> res = new ArrayList<>();
    List<String> requestAttendees = new ArrayList(request.getAttendees());

    for (Event event : events) {
      int rangeStart = event.getWhen().start();
      int rangeEnd = event.getWhen().end();

      List<String> eventAttendees = new ArrayList(event.getAttendees());

      boolean mustFill = false;

      for (String attendee : eventAttendees) {
        if (requestAttendees.contains(attendee)) {
          mustFill = true;
          break;
        }
      }
      if (mustFill == true) {
        for (int i = rangeStart; i < rangeEnd; i++) {
          minutes[i] = 1;
        }
      }
    }

    for (int i = 0; i < MINUTES_IN_ONE_DAY; i++) {
      if (minutes[i] == 0) {
        int curStart = i;
        int curEnd = i;

        for (int j = i; j < MINUTES_IN_ONE_DAY; j++) {
          if (minutes[j] == 1) {
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
}
