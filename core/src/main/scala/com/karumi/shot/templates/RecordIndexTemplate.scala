package com.karumi.shot.templates

object RecordIndexTemplate {
  def recordIndexTemplate(
      title: String,
      summaryResult: String,
      summaryTableBody: String
  ): String = {
    // language=HTML
    s"""
<!doctype html>
<html>
<head>
    <title>Shot record results</title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/materialize/0.100.2/css/materialize.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
</head>
<style>
    body {
        display: flex;
        min-height: 110vh;
        flex-direction: column;
    }

    main {
        flex: 1 0 auto;
    }
</style>
<body>
<nav>
    <div class="nav-wrapper indigo darken-3">
        <a href="#" class="brand-logo left">$title</a>
        <ul id="nav-mobile" class="right hide-on-med-and-down">
            <li><a href="https://github.com/karumi/shot">Shot Documentation</a></li>
        </ul>
    </div>
</nav>
<main class="container">
    <div class="section">
        <table class="highlight responsive-table">
            <thead>
            <h5 class="indigo-text darken-3">Shot record results: $summaryResult</h5>
            <tr>
                <th>Test name</th>
                <th>Screenshot</th>
            </tr>
            </thead>
            <tbody>
            $summaryTableBody
            </tbody>
        </table>
    </div>
</main>
<footer class="page-footer indigo darken-3">
    <div class="container">
        <div class="row">
            <div class="col l6 s12">
                <h5 class="white-text">Shot record report</h5>
                <p class="grey-text text-lighten-4">
                    Report generated automatically by Shot. Contains the execution result of the shot screenshots
                    verification stage.
                </p>
                <p>The project documentation can be found in <a class="grey-text text-lighten-3"
                                                               href="https://github.com/karumi/shot">this GitHub
                    repository.</a>
                </p>
            </div>
            <div class="col l4 offset-l2 s12">
                <h5 class="white-text">Contributions</h5>
                <p class="grey-text text-lighten-4">
                    We are trying to improve our plugin in order to make our screenshot tests execution easy to use. Do
                    you wanna help? Feel free to propose any change or send a pull request in the <a
                        class="grey-text text-lighten-3"
                        href="https://github.com/karumi/shot">project GitHub
                    repository.</a>
                </p>
            </div>
        </div>
    </div>
    <div class="footer-copyright">
        <div class="container">
            Powered by <a href="https://karumi.com"> <img width="30px"
                                                          src="http://static1.squarespace.com/static/56dd9af5e707eb815a7f9c86/t/56fbab3d2eeb817eecc15a0c/1502355279546/?format=1500w"/></a>
        </div>
    </div>
</footer>
<script src="https://cdnjs.cloudflare.com/ajax/libs/materialize/0.100.2/js/materialize.min.js"></script>
</body>
</html>
"""
  }
}
