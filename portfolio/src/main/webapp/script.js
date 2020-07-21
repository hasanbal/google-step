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

/**
 * Adds a random greeting to the page.
 */
function addRandomGreeting() {
  const greetings =
      ['Hello world!', '¡Hola Mundo!', '你好，世界！', 'Bonjour le monde!'];

  // Pick a random greeting.
  const greeting = greetings[Math.floor(Math.random() * greetings.length)];

  // Add it to the page.
  const greetingContainer = document.getElementById('greeting-container');
  greetingContainer.innerText = greeting;
}

function getRandomImage(){
    const imageId = Math.floor(Math.random()*5) + 1;
    const imageTag = document.getElementById('randomImg');

    imageTag.src = "images/ig"+imageId.toString()+".png";
}

function popupDetail(id){
    const detail = document.getElementById('detail-'+id.toString());
    const button = document.getElementById('button-'+id.toString()); 
    
    if(button.innerHTML == "Show Details"){
        detail.style.visibility = "visible";
        button.innerHTML = "Hide Details";
    }else{
        detail.style.visibility = "hidden";
        button.innerHTML = "Show Details";
    }
}

async function getFromServer() {
    const response = await fetch('/data');
    var comments = await response.text();
    comments = JSON.parse(comments);

    const commentsListElement = document.getElementById('comments');
    commentsListElement.innerHTML = '';
    commentsListElement.appendChild(
        createListElement('Comment1: ' + comments[0]));
    commentsListElement.appendChild(
        createListElement('Comment2: ' + comments[1]));
    commentsListElement.appendChild(
        createListElement('Comment3: ' + comments[2]));
}

function createListElement(text) {
    const liElement = document.createElement('li');
    liElement.innerText = text;
    return liElement;
}

async function loadComments(){
    const response = await fetch("/comments");
    var comments = await response.text();
    comments = JSON.parse(comments);

    const commentsListElement = document.getElementById('comments');
    commentsListElement.innerHTML = '';
    for(var i=0;i<comments.length;i++){
        commentsListElement.appendChild(createListElement("Comment"+i.toString() + ": " + comments[i]));
    }
}