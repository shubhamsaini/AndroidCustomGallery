AndroidCustomGallery
====================

Abstract
It is intended that a micro-controller, an accelerometer and a Bluetooth chip embedded into a ring be interfaced with an Android based smart phone. The smart phone app currently has an image gallery and a music player in it. The ring is able to control the app wirelessly allowing the user to browse through the images, skip songs, control the music volume, control the mobile phone profile and make an SOS call in case of any emergency through simple hand gestures. All these options can also be accessed via the touch screen well should the user decide not to use the ring.
The app was developed using the Android Development Tools (ADT) version 22 built within the Eclipse IDE. The app will initiate connection to the ring via Bluetooth and start taking commands from it upon successful connection. The commands will be received in the form of strings, and if the command matches with the commands hardcoded into the app, appropriate action will be performed.

Background
The main idea behind the project is to interface a networked ring that not only serves the purpose of aesthetics but also have certain sensing and cognitive abilities to an Android based smart phone. The ring developed at the centre communicates to a smart phone via Bluetooth and control certain functionality. The necessary operations are carried out through user’s hand gestures.

Problem Statement
The aim is to develop an android app consisting of an image gallery and a simple music player. The app will initiate connection to the ring via Bluetooth and start taking commands from it upon successful connection. The commands will be received in the form of strings, and if the command matches with the commands hardcoded into the app, appropriate action will be performed.

Motivation
The motivation for our project was to create an intuitive input system that would be easy and fun to use for mobile and computer applications. The ring enables a person to use a computer or mobile phone by performing intuitive hand gestures. Our gesture input system can be conveniently used by anyone who wishes not hold the mobile phone in their hand or controlling it while it is kept in pocket or on a desk. It can also be used by someone who wishes not to be tied down to a desk when using a computer, making it perfect for giving presentations or web surfing from the couch.

Related work
A recent Master of Design (MDes) project in the Centre for Product Design and Manufacturing (CPDM) demonstrated the feasibility of a ring and a locket that have the following functionalities: (i) ability to put a mobile phone into the silent mode and turn it back to the ringing mode at the push of a button on the ring or the locket; and ability to play music on the phone by using the ring or the locket so that a misplaced phone can be found; (ii) measure the pulse rate of the person wearing the ring; and (iii) exchanging contact information by shaking hands with another person owning the same type of jewellery.

Challenges
Maintaining a stable Bluetooth connection between the ring and the mobile phone posed a serious problem. The android API provides no methods that trigger an event in case of a disruption in Bluetooth connection. The solution is to determine the time a communication between the ring and mobile phone took place and attempting to re-connect if it exceeds a certain time.

Another challenge was to display huge images in the gallery. The images to be displayed are first decoded into Bitmap format, and then displayed. So if an image is of 5MB, the converted BMP format can be well over 20MB. This leads to OutOfMemoryException. This was solved by scaling all the images to ½ the original size. Although it caused some loss in image quality, it is the only known solution.

One of the features of the app is to dial an emergency number when the ring is tapped twice. Since a single tap gesture is associated with change in mobile phone profile, in case of a double tap the mobile switched the profile and then dialed the emergency number. This was solved by waiting for a second tap for 1.5 seconds. If no second tap gesture is performed within this time, mobile phone profile was toggled.

Essence of the Project
The backbone of this entire project is the Bluetooth functionality. In the initial phase of the project, only a very basic system that initiated Bluetooth connection and sent/received data was implemented. Only when this communication system was established did we move onto more complex system.

Two separate modules: Image Gallery and Music Player were built independently. All the features needed in the final product were put into them (except the Bluetooth control). These modules were then put on top of the Bluetooth communication system and the modules were integrated.





Assumptions
•	The images stored on the mobile device that are to be displayed in the gallery are of JPG,PNG or BMP format only.
•	The songs stored on the mobile device to be played in the music player are of MP3 format only.
•	The ring is already paired with the mobile phone.
