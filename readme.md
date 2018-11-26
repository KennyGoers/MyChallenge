Here lies the result of my code challenge. As with any of these things there is a wonder in what the evaluator is actually interested in and never any feedback following to know if architecture is more important and if so what architecture do they like and is there any room for discussion and if there are actual pros and cons (There are no real right answers here, only what conventions the project uses).

I concentrated on the function of the app and having to decode the requirements and making sure those were met. Then tried to organize the code in a readable manor, and just a basic MVVM architecture. I made the assumption that the newest technologies would be desired so I ported a wizard created app to AndroidX and the current Material Design components and worked from there.

Things I would have liked to improve upon:

* More design flourish, a real app has design input and while I have skill in that area, tight time constraints limit the output. I added some basic activity animations, but then decided on a few element transitions (Movie title and location) instead.
* After iterating on the UX, I think instead of a collapsible list of locations within a movie, I would have done just a list of movies, seleting the movies would have shown a map with pins for each location. I believe the user would have gotten the most from this interface.
* A persistence layer would have been nice, but data organization changed a few times in iterating over the UI.
* A ViewModel for the Detail fragment should be added along with unit tests, only one unit test was provided in my sample code

The Map API key is left blank, if you need instructions on how to activate this in your own account, let me know.

Further discussion about the whats and whys can be gotten from me in an in person interview.
