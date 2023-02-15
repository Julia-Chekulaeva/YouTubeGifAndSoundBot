## YouTube Gif And Sound Bot

This is an app which converts youtube videos to gifs and files with sound.
Here is the description of how to use this bot.
Unfortunately, the gifs larger than 30 seconds most probably couldn't get loaded
because files larger than 50 MB cannot be sent in Telegram.

### Commands

`/start` - prints a description of the bot.

`/commands` - prints the list of commands.

`/gif youtube_url [[[h:]m:s]-[[h:]m:s]]` -
loads a gif from a fragment of YouTube video with start and end timesteps.

`/audio youtube_url [[[h:]m:s]-[[h:]m:s]]` -
loads a gif from a fragment of YouTube video with start and end timesteps.

Brackets [] indicate that the fragment inside them may be dropped.

`youtube_url` should match this regex:
`https?://((www\.)?youtube\.com/(watch\?v=|shorts/)|youtu\.be/)[\w\d_-]+([&?].*)?`

Examples of valid commands `/gif` and `/audio` and outputs for them:

    /gif https://youtu.be/6ofIPBp_mXo -00:5

[gif1](src/main/resources/1.gif.mp4)

    /gif https://youtu.be/6ofIPBp_mXo 0:40-0:1:0

![gif2](src/main/resources/2.gif)

    /gif https://youtu.be/6ofIPBp_mXo 2:20-

![gif3](src/main/resources/3.gif)

    /gif https://youtu.be/gVkyBu_v7vA -

[gif4](src/main/resources/4.gif.mp4)

    /gif https://youtube.com/shorts/mx9n1FmW67k?feature=share

[gif5](src/main/resources/5.gif.mp4)

    /audio https://youtu.be/6ofIPBp_mXo

[audio1](src/main/resources/6.wav)

    /audio https://youtu.be/6ofIPBp_mXo 00:40-01:00

[audio2](src/main/resources/7.wav)

    /audio https://youtu.be/gVkyBu_v7vA

[audio3](src/main/resources/8.wav)