# CrunchyTransmitter - APP

Welcome to the CrunchyTransmitter app. First of all, if you only want to use the app, just click [here](https://play.google.com/store/games?gl=DE) to install.


### What is my job?
I'm here to give you a quick and easy way to see the episodes that are coming out for each anime this week. I also give you the opportunity to subscribe to the anime you like and then get notifications when a new episode is released.

I also have the cool feature that when you click on a notification, you are then redirected directly to your Cruchyroll app. This also works if you hold down on an episode and click on "Anschauen".

## Support

For support please write me on Discord (milschschnitte).

I welcome any constructive suggestions for the project and will be happy to develop it further on request. In case of security problems or bugs please create an issue. The same applies to the app.

Since I privately pay for the server costs and the publishing on Google, I would be happy about a little support.

[![PayPal](https://upload.wikimedia.org/wikipedia/commons/thumb/b/b5/PayPal.svg/2560px-PayPal.svg.png)](https://www.paypal.com/paypalme/JoshuaSchlesiger?country.x=DE&locale.x=de_DE)


## Techstuff

The app was developed using Flutter. Dart was therefore used as the programming language.

### App

The app has the following tasks as described above:

* Provide a list describing the release of the anime episodes
* Generate Google FCM tokens and send them to the server
* Provide subscription of anime and send them to the server
* Receive notification from Google FCM
* Redirect to the Crunchyroll app
* Welcome screen describing the functions
* Settings page to be able to specify your own server url




## Deploy your own app

### Requirements

* Flutter 
* Dart SDK version: 3.4.1
* Google FCM account (described in the main branch)

### Instructions to deploy
In the project folder under lib\config you will find the file example_config.dart. You must rename or copy this to config.dart. Now you need the password from the server which you have defined in the .properties. In addition, the later URL of the server (in my case: https://crunchytransmitter.ddns.net/CrunchyTransmitter/ ). 
In the main branch I have described that you get 2 files provided by Google FCM. Now you have to place the other file (google-services.json) under android\app\. You should then be able to build your project.


## License

This project is licensed under [apache-2.0](https://choosealicense.com/licenses/apache-2.0/)
| Permissions      | Conditions | Limitations       |
|-----------|-------|-------------|
 |🟢 Commercial use | 🔵License and copyright notice|  🔴Liability |
 |🟢Distribution| 🔵State changes| 🔴Trademark use |
 |🟢Modification | | 🔴Warranty |
 |🟢Patent use | 
 |🟢Private use |


