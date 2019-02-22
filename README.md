Overview

This Android app views recipes and their steps in video format. Media loading classes, UI tests, and third party libraries are used, as well as a home screen widget.

Project Overview

    Take app from functional to production ready.

    Baking App

        User selects a recipe and watches a video with guided steps for how to complete it.

        JSON payload provided from Udacity servers.
        https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json

        Not all steps in the recipe have a video, or even a picture, and the app should handle these cases gracefully.

Requirements

    RecyclerView

    Error cases

    Accessibility

        Master/Detail Flow pattern
        Fragments to create a responsive design for both phones and tablets

    Localization

    UI Testing

        Espresso to test aspects of the UI.

    Widget

        Displays ingredient list for a desired recipe.

    Third party libraries

        At least one third party library is used.
        This can be a library to assist in the creation of a ContentProvider, a UI binding library, etc.

        Picasso / Glide
        Butterknife
        Timber

    Display videos

        ExoPlayer with proper initialization and release.

Submission

    Extract all hardcoded strings and dimensions to appropriate directories.
    Remove all API keys from public repository, add note in README. (No API keys for this project.)
    Gradle > Clean project

    Rubric
    https://review.udacity.com/#!/rubrics/829/view
