# Oso

This is a light, anonymous ,amnesic, safety tracking.

The aim is to have an app which :
- is battery restpectfull: the less power it need the better. 
- is loginless: I do not like to have to create an account on every service I use. So this one in design to be donwload and play!
- is anynonymous: when you enable the tracking , the server give you a random ID, which will change on your new track.
- is amnesic: The tracks will be remove from the server after X (need to be define) days.
- is privacy respectfull: No private or usage data  are store or send. Just the trackpoints ( can be redirect to your own server)
- no ads no analytics stuff :  light , simple and privacy respectfull we said  !  

What Oso is not:
- a maps apps. No maps display, there are thousand of apps which do that very well ( c.f Osmand).
- a stealth tracker. 

It is composed by 3 part:

## A loger 
Require: Gps on device and autorization.

Android service which log every time interval ( > 1min)  the position ( Latitude, Longitude, Altitude, Accuracy), battery level, and network strengh. to an SQLite database.

## A Tracker
Require : Loger to be on, and data mobile connection.
Which send  the position to  a website then mark points as sent in the SQLite database.

## A Watcher.
Require : Loger to be on, and an accelerometer.
A service  able to send sms  to people if your phone is suspiciously still.


## Server side
All data are sent to a server, which is also opensource:  https://github.com/fredvol/OsoWeb


##License:
Still to be define but should be GPL V3.

WORK IN SLOW PROGRESS ....

## Coding details:
Started with Android studio 2.2.3

Target is Android >=5.
