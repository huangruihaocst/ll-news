# **LLNews**
An Android application used for reading news. Source code at [Git9](https://git.net9.org/hrh14/LLNews).
黄锐皓 2014011385

## Functions
- List categories
- List news under certain category
- View news' image and content
- Adjust subscription
- Share a certain news (Multi-target)
- Refresh news list on swiping
- Get older news on reaching the end of the list
- Add news to favorites and view them in a list
- Simplify a certain website, making it easier to read (Creative, in DataAccessor.java)
- Read the content of a certain news (TTS) (Creative, in NewsActivity.java)
- Add word filter to the news list (if a word (or some words) occurs too much time, give the news a special marker) (Creative, in almost all the files)

## Features
- Material Design
- Share to WeChat
- Open Source Library: Volley, SugarORM, Picasso, Readability
- Cache: memory → database → Internet
- My own node.js server

## About Database
I used SugarORM (based on sqlite) to make my work easier.
Columns are the attributes that a piece of news should have.

## Cautions
- For simplifying, reading and word filter, must run the server in advance
- To run the server, replace the server's ip with its real ip in app/src/main/java/com/ihandy/a2014011385/helpers/DataAccessor.java

## About How to Run the Server
1. `cd LLNews`
2. `sudo apt-get install nodejs`
3. `sudo apt-get install npm`
4. `npm install node-readability`
5. `nodejs Readability.js`

## What's More
I took part in the final presentation, so I attached the presentation's ppt containing the implementation of the creative functions to simplify my documentation.