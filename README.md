
# CrunchyTransmitter 
Welcome to me the CrunchyTransmitter

## Why do I exist?!?
I came about because my mom Crunchyroll simply had no means of notification when one of her children was born (episodes released). My master, the MilschSchnitte, always wanted to watch the latest episodes in the evening and be notified when one came out. Also, mom's website takes so long to load because of this stupid JavaScript.... 

Use me if you would like to receive notifications about your subscribed anime and also like to watch in advance when an episode will be released



## Support

For support please write me on Discord (milschschnitte).

I welcome any constructive suggestions for the project and will be happy to develop it further on request. In case of security problems or bugs please create an issue. The same applies to the app.

Since I privately pay for the server costs and the publishing on Google, I would be happy about a little support.

[![PayPal](https://upload.wikimedia.org/wikipedia/commons/thumb/b/b5/PayPal.svg/2560px-PayPal.svg.png)](https://www.paypal.com)



## Techstuff

The project is divided into 2 parts. The **"main"** branch is the **server** (Java) and the branch **"flutter_project"** is the **app** for the cell phone written in Dart

### Server

The server was created using the **Spring** framework and has 3 **automated tasks**.
* To address the Crunchyroll interface every 5 minutes and process the data so that it can be saved in a postgres
* Check every minute whether an episode's release date has been reached and notify subscribers via FCM
* Query the database every 5 minutes to see which episodes of the anime will be released this week

In addition, the server should provide **1 get-interface (/anime)** where clients can download the week's data.
In addition, **2 post-interfaces** are required for the notification system. The first interface (**/registerToken**) is required so that clients can register their FCM token with my server and then use the second interface (**/updateAnimeSub**) to manage their subscriptions. 

### Properties

The server offers a configuration option using properties. Copy the application.properties_example to application.properties. I will now explain the individual properties:

* **jakarta.persistence.jdbc.url** <- URL to your Postgres with database, the same must also be in spring.datasource.url
    
* **spring.datasource.url** <- URL to your Postgres with database
* **spring.datasource.username** <- Database user
* **spring.datasource.password** <- Database password
* **spring.datasource.driver-class-name** <- driver org.postgresql.Driver

* **crunchyroll.seasonURL** <- Every quarter this link must lead to the Crunchyroll interface
* **spring.api.key** <- A password is required here for client and server so that I have authorization. Mostly 4096 characters

* **spring.api.token.refill** <- Recharge of the number of opportunities per minute at which a client can send their FCM token
* **spring.api.token.storage** <- Number of options for using the interface

* **spring.api.animeget.refill** <- Recharge of the number of opportunities per minute a client can access the /anime page
* **spring.api.animeget.storage** <- Number of options for using the interface

* **spring.api.animesub.refill** <- Recharge of the number of opportunities per minute at which a client can change their subscriptions
* **spring.api.animesub.storage** <- Number of options for using the interface
## Deployment

### Requirements
* Java version: 20.0.2
* Maven version 3.9.6
* Tomcat version 10.1.24
* Postgres version 15.6

### Database preparation 

I use a Postgres database. I have written a file in the project path which gives you the commands to create your database correctly. The file is called postgre.commands

### Instructions to deploy

First of all, you should know that the project is currently running with Google FCM. 

To create your own server you need a project at [Google FCM](https://firebase.google.com/). From there you will also get the 2 files for your server and the app. The server file, which in my case is called: crunchy-transmitter-firebase-adminsdk-fondb-7cc6dde6c0.json, must be placed in the "resources" folder. Then change the name in the java file under src\main\java\de\milschschnitte\crunchytransmitter\fcm\FirebaseConfig.java.

The next step is to create the properties file as described above under Properties. The server should now be able to be started as .war using Tomcat.

You can use the following commands for this:

In your project just write:

```bash
  mvn clean install
  cd target/
  cp CrunchyTransmitter-1.0.war /opt/tomcat/webapps/
```

To ensure that **your own server** is also controlled in your app, please take a look at the readme of the branch : flutter_project
## License

This project is licensed under [apache-2.0](https://choosealicense.com/licenses/apache-2.0/)
| Permissions      | Conditions | Limitations       |
|-----------|-------|-------------|
 |🟢 Commercial use | 🔵License and copyright notice|  🔴Liability |
 |🟢Distribution| 🔵State changes| 🔴Trademark use |
 |🟢Modification | | 🔴Warranty |
 |🟢Patent use | 
 |🟢Private use |


