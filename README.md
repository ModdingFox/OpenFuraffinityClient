This is an unofficial opensource client for Furaffinity.<br />
<br />
This application is provided as-is with no warranties/guarantees other than the following.<br />
&nbsp;&nbsp;&nbsp;&nbsp;This application is provided for free and you will never have to pay for it, if you have paid for this application request a refund from whoever sold it to you.<br />
&nbsp;&nbsp;&nbsp;&nbsp;No feature of this application now or in the future will ever require a payment to use or be locked behind a subscription of any kind.<br />
&nbsp;&nbsp;&nbsp;&nbsp;Advertisements(once added) will only come from Furaffinity directly and will always have the option to be disabled or enabled by the user.<br />
&nbsp;&nbsp;&nbsp;&nbsp;That being said Furaffinity has server costs and this application does not, we will respect that fact and once added they will be enabled by default.<br />
<br />
This app is pretty rough at the moment. Most things work the way they should though there are a few bugs here and there.Much of the UI design is also pretty basic and will hopefully be made to look much prettier in later releases.<br />
<br />
The goal of the OpenFuraffinityClient project is to have a client for Furaffinity that is owned/maintained by its users.<br />
All code for the official google playstore releases will always be avaliable for viewing and modification here on version tagged branches.<br />
Users who are also developers are certainly encouraged to pull the code down build the application and tinker around with it.<br />
The github repo is setup to allow for pullrequests which will allow developers to contribute updates and changes they have made to the application back to the community.<br />
Releases will be sporadic unless a critical fix is needed.<br />
Note all pullrequests from developers new to the project must inclued the submitting developers furaffinity user page link.<br />
All contributors to this application will be listed in the about page.<br />
<br />
These sections are responsible for grabbing data from the various web pages and any prep needed for the ui to use. Likely if reworked enough there could be useful as standalone java libs for accessing FA.<br />
&nbsp;&nbsp;&nbsp;&nbsp;open.furaffinity.client.pages: Contains any read only type functionallity.<br />
&nbsp;&nbsp;&nbsp;&nbsp;open.furaffinity.client.submitPages; Contains any write functionallity.<br />
&nbsp;&nbsp;&nbsp;&nbsp;open.furaffinity.client.pages and open.furaffinity.client.submitPages depend on open.furaffinity.client.abstractClasses.abstractPage and a few classes from open.furaffinity.client.utilities.<br />
