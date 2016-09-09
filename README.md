# **LLNews**
An Android application used for reading news. Source code at [Git9](https://git.net9.org/hrh14/LLNews).

## Functions
- List categories
- List news under certain category
- View news' image and content
- Adjust subscription
- Share a certain news
- Refresh news list on swiping
- Get older news on reaching the end of the list
- Add news to favorites and view them in a list
- Simplify a certain website, making it easier to read
- Read the content of a certain news (TTS)
- Add word filter to the news list (if a word (or some words) occurs too much time, give the news a special marker)

## Features
- Material Design
- Open Source Library: Volley, SugarORM, Picasso, Readability
- Cache: memory → database → Internet
- My own node.js server

## Cautions
- For simplifying, reading and word filter, must run the server in advance
- To run the server, replace the server's ip with its real ip in app/src/main/java/com/ihandy/a2014011385/helpers/DataAccessor.java

## About How to Run the Server
1. `sudo apt-get install nodejs`
2. `nodejs Readability.js`

## What's More
I took part in the final presentation.