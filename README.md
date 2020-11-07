# Webscraper

This is a Project to webscrap a website. I have used JSoup library for this.
THe URL passed from main methpd will be scanned for any sub URLs, this scan happens till depth 4.
Then from those pages we will search for most frequently occuring 10 words and most frequently
occuring 10 pairs of words. The result will be printed in console.

Since this is a maven project we need 'clean install' the project for running. 
We can change the URL in main method to test other URLs.
