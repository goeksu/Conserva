const admin = require('firebase-admin');
const functions = require('firebase-functions');
admin.initializeApp(functions.config().firebase);
var db = admin.firestore();

var users = db.collection('users');
var calls = db.collection('calls');

exports.iotAktif = functions.https.onRequest((req, res) => {
  const iotid = req.query.text;
var FieldValue = admin.firestore.FieldValue;
  var iotbilgi = users.doc(iotid).get()
  .then(doc => {
    if (!doc.exists) {
      console.log('kullanici yok');
    } else {
      console.log('bilgiler:', doc.data());
      
      var cagri = db.collection('calls').doc(doc.data().id).set({
  id: doc.data().id,
  adres: doc.data().adres,
  tip: doc.data().tip,
  sahip: doc.data().sahip,
  update: FieldValue.serverTimestamp(),
  konum: doc.data().konum
}).then(ref => {
  console.log('Eklendi ', ref.id);
});

      }
  })
  .catch(err => {
    console.log('hata', err);
  });
  res.send("ekleme islemi");
});

exports.iotPasif = functions.https.onRequest((req, res) => {
  const iotid = req.query.text;

  var iotbilgi = users.doc(iotid).get()
  .then(doc => {
    if (!doc.exists) {
      console.log('kullanici yok');
    } else {
      console.log('bilgiler:', doc.data());
      var deleteDoc = db.collection('calls').doc(iotid).delete();
      }
  })
  .catch(err => {
    console.log('hata', err);
  });
res.send("silme islemi");
});
