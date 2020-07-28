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

/* eslint-disable no-unused-vars */

let lastLimit = -1;
let lastLanguage = 'original';

/** Get random image from gallery */
function getRandomImage() {
  const imageId = Math.floor(Math.random() * 5) + 1;
  const imageTag = document.getElementById('randomimg');

  imageTag.src = 'images/ig' + imageId.toString() + '.png';
}
/** Show/hide popup
* @param {int} id popup section id
*/
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

/** Create list element
* @param {string} text list elements string
* @return {object} list element
*/
function createListElement(text) {
  const liElement = document.createElement('li');
  liElement.innerHTML = text;
  return liElement;
}

/** Load comments from server
* @param {int} limit limit of comments count
* @param {int} admin Whether request came from admin panel or not.
* @param {String} language Translate the comments by language code.
*/
async function loadComments(limit = -1, admin = 0, language = 'original') {
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

  for (let i = commentsJson.length - limit; i < commentsJson.length; i++) {
    commentsJson[i] = JSON.parse(commentsJson[i]);

    if (language != 'original') {
      commentsJson[i].comment =
      await translate(commentsJson[i].comment, language);
    }

    let element = commentsJson[i].username + ': ' + commentsJson[i].comment;

    if (admin == 1) {
      element += ' <button onclick="deleteComment(';
      element += commentsJson[i].timestamp;
      element += ');">Delete</button>';
    }

    commentsListElement.appendChild(createListElement(element));
  }
}

/** Delete comments.
* @param {long} timestamp The timestamp of message.
*/
async function deleteComment(timestamp) {
  const response = await fetch('/delete-comment?timestamp='+timestamp);
  console.log(response);
  location.reload();
}

/** Limit comments count.
* @param {object} limitObject get value of limitObject
*/
function limitComments(limitObject) {
  const limit = limitObject.value;
  lastLimit = limit;

  loadComments(limit, 0, lastLanguage);
}

/** Check login status and hide/show the comments. */
async function checkLogin() {
  const response = await fetch('/login-status');
  const resText = await response.text();
  const resJson = JSON.parse(resText);

  const commmentsDiv = document.getElementById('comments-div');
  const commentsError = document.getElementById('comments-error-div');
  const commentsErrorMsg = document.getElementById('comments-error-msg');
  const username = document.getElementById('username');
  const logout = document.getElementById('logout');

  if (resJson.login == 1) {
    commmentsDiv.style.display= 'block';
    commentsError.style.display= 'none';
    username.innerText = resJson.email;
    logout.innerHTML = '<br><a href="' + resJson.logoutUrl + '">Logout</a>';
  } else {
    commmentsDiv.style.display= 'none';
    commentsError.style.display= 'block';

    let error = 'You should login to read and write comments.';
    error += ' <a href="';
    error += String(resJson.loginUrl);
    error += '">Login</a>';
    commentsErrorMsg.innerHTML = error;
    console.log(commentsErrorMsg);
  }
}

/** Translate the text
  @param {String} text Text for translate
  @param {String} language Language Code for translate
*/
async function translate(text, language) {
  const query = '/translate?text=' + text + '&language=' + language;
  const response = await fetch(query);
  const resText = await response.text();

  return resText;
}

/** Change the selected language
  @param {object} languageObject Language Selection
*/
function changeLanguage(languageObject) {
  const language = languageObject.value;
  lastLanguage = language;

  loadComments(lastLimit, 0, language);
}
