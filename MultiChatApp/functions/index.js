'use strict';

const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);
const request = require('request-promise');

// List of output languages.
const LANGUAGES = ['es', 'ru', 'ja'];

// Translate an incoming message.
exports.translate = functions.database.ref(`/message/{messageID}`).onCreate(event => {
  const snapshot = event.data;

  console.log('Original', snapshot);
  const text = event.data.child('text').val();
  const enText = event.data.child('en').val();
  const original_language = event.data.child('language').val();
  console.log("snapshot original text : " + text);
  console.log("snapshot language : ", original_language);

  const promises = [];
  for (let i = 0; i < LANGUAGES.length; i++) {
    var language = LANGUAGES[i];
    if (language !== original_language) {
      promises.push(createTranslationPromise(original_language, language, enText, snapshot));
    }
  }
  return Promise.all(promises);
});

// URL to the Google Translate API.
function createTranslateUrl(source, target, payload) {
  console.log("Translating from", source, target, payload);
  return `https://translation.googleapis.com/language/translate/v2?key=${functions.config().firebase.apiKey}&target=${target}&q=${payload}`;
}

function createTranslationPromise(source, target, message, snapshot) {
  const key = snapshot.key;
  return request(createTranslateUrl(source, target, message), {resolveWithFullResponse: true}).then(
      response => {
        if (response.statusCode === 200) {
          const data = JSON.parse(response.body).data;
          console.log('Translation: ', data);
          console.log('Translation to save: ', data.translations[0].translatedText.toString());
          return admin.database().ref(`/message/${key}/${target}`)
                               .set(data.translations[0].translatedText.toString());
        }
        throw response.body;
      });
}