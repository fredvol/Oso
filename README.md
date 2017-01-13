# Oso

This is a light, anonymous ,amnesic, safety tracking.

The aim is to have an app which :
- is battery restpectfull: the less power it need the better. 
- is loginless: I do not like to have to create an account on every service I use. So this one 
- is anynonymous: when you enable the tracking , the server give you a random ID, which will change on your new track.
- is amnesic: The tracks will be remove from the server after X ( need to be define) days.



It is compose by 3 part:

## A loger 
Require : Gps on device and autorization.

Android service which log every time interval ( > 1min)  the position ( Latitude, Longitude, Altitude, Accuracy), battery level, and network strengh. to an SQLite database.

## A Tracker
Require : Loger to be on, and data mobile connection.
Which send  the position to  a website

## A Watcher.
Require : Loger to be on, and an accelerometer.
A service  able to send sms  to people if your phone is suspiciously still.


WORK IN SLOW PROGRESS ....

Started with Android studio 2.2.3
