// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the 'License');
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an 'AS IS' BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/*exported getRandomImage, popupDetail, limitComments */

function getRandomImage() {
  const imageId = Math.floor(Math.random() * 5) + 1;
  const imageTag = document.getElementById('randomImg');

  imageTag.src = 'images/ig' + imageId.toString() + '.png';
}

function popupDetail(id) {
  const detail = document.getElementById('detail-' + id.toString());
  const button = document.getElementById('button-' + id.toString());

  if (button.innerHTML == 'Show Details') {
    detail.style.visibility = 'visible';
    button.innerHTML = 'Hide Details';
  } else {
    detail.style.visibility = 'hidden';
    button.innerHTML = 'Show Details';
  }
}

function createListElement(text) {
  const liElement = document.createElement('li');
  liElement.innerText = text;
  return liElement;
}

async function loadComments(limit = -1) {
  const response = await fetch('/comments');
  const comments = await response.text();
  const commentsJson = JSON.parse(comments);

  const commentsListElement = document.getElementById('comments');
  commentsListElement.innerHTML = '';

  if (limit == -1) {
    limit = commentsJson.length;
  } else {
    limit = Math.min(limit, commentsJson.length);
  }

  for (var i = commentsJson.length - limit; i < commentsJson.length; i++) {
    commentsJson[i] = JSON.parse(commentsJson[i]);

    const element = commentsJson[i].username + ': ' + commentsJson[i].comment;
    commentsListElement.appendChild(createListElement(element));
  }
}

function limitComments(limitObject) {
  const limit = limitObject.value;

  loadComments(limit);
}
