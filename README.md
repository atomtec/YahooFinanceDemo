This is a demo app made using YahooFinance SDK .
Used for adding and and vieweing stock quotes
Currently does not support delete.

Architecture

This uses MVVM architecture
When the app is in fore ground it does continous sync to get the real time updates

Once in foreground immediately fetches data
The data is synced directly to DB from where the view is updated

Demo
![YahooFinance Demo](demo/demo.gif)