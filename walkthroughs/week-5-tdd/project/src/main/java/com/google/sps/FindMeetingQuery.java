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
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    long duration = request.getDuration();
    int[] minutes = new int[1441];
    List<TimeRange> res = new ArrayList<>();
    List<String> requestAttendees = new ArrayList(request.getAttendees());

    for (Event event : events) {
      int rangeStart = event.getWhen().start();
      int rangeEnd = event.getWhen().end();

      List<String> eventAttendees = new ArrayList(event.getAttendees());

      int mustFill = 0;

      for (String attendee : eventAttendees) {
        if (requestAttendees.contains(attendee)) {
          mustFill = 1;
          break;
        }
      }
      if (mustFill == 1) {
        for (int i = rangeStart; i < rangeEnd; i++) {
          minutes[i] = 1;
        }
      }
    }

    for (int i = 0; i < 1440; i++) {
      if (minutes[i] == 0) {
        int curStart = i;
        int curEnd = i;

        for (int j = i; j < 1440; j++) {
          if (minutes[j] == 1) {
            i = j;
            break;
          }
          i = j;
          curEnd = j;
        }
        int curDuration = curEnd - curStart + 1;

        if (duration <= curDuration) {
          TimeRange asd = TimeRange.fromStartDuration(curStart, curDuration);
          res.add(asd);
        }
      }
    }
    return res;
  }
}
