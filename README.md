# WhisperTimeSync
Synchronize Whisper's timestamps over an existing accurate transcription

# Install

```
git clone https://github.com/EtienneAb3d/WhisperTimeSync.git

cd WhisperTimeSync

virtualenv -p python3 ../venvWhisperTimeSync
source ../venvWhisperTimeSync/bin/activate

pip install -U openai-whisper

sudo apt update && sudo apt install ffmpeg
```

# Transcribe

```
python transcribeExample.py 

Output:
==========
Loading Whisper model...
Transcribing: data/KatyPerry-Firework.mp3 ...
 Do you ever feel like a plastic bag? Drifting through the wind, wanting to start again? Do you ever feel, feel so paper-thin? Like a house apart, one blow from caving in? Do you ever feel already buried deep? Six feet under screens and no one seems to hear a thing. Do you know that there's still a chance for you? Because there's a spark in you. You just got to ignite the light and let it shine. Just own the night like the 4th of July. Cause baby you're a firework. Come on, show them what you're worth. Make them go ah, ah, ah, as you shoot across the sky. Baby, you're a firework. Come on, let your colors burst. Make them go ah, ah, ah. You're going to leave them all in awe, awe, awe. You don't have to feel like a wasted space. Your original cannot be replaced. If you only knew what the future holds. After a hurricane comes a rainbow. Maybe a reason why all the doors are closed. So you could open one that leads you to the perfect road. Like a lightning bolt, your heart will blow. And when it's time, you'll know you just got to ignite the light and let it shine. Just own the night like the 4th of July. Cause baby you're a firework. Come on, show them what you're worth. Make them go ah, ah, ah, as you shoot across the sky. Baby, you're a firework. Come on, let your colors burst. Make them go ah, ah, ah. You're going to leave them all in awe, awe, awe. Boom, boom, boom. Even brighter than the moon, moon, moon. It's always been inside of you, you, you. And now it's time to let it through. Cause baby you're a firework. Come on, show them what you're worth. Make them go ah, ah, ah, as you shoot across the sky. Baby, you're a firework. Come on, let your colors burst. Make them go ah, ah, ah. You're going to leave them all in awe, awe, awe. Boom, boom, boom. Even brighter than the moon, moon, moon. Boom, boom, boom. Even brighter than the moon, moon, moon.
Saving: data/KatyPerry-Firework.mp3.srt ...
==========
```

# Synchronize

```
java -jar distrib/WhisperTimeSync.jar data/KatyPerry-Firework.mp3.srt data/KatyPerry-Firework.txt

Output (data/KatyPerry-Firework.txt.srt):
==========
1
00:00:00,000 --> 00:00:18,020
Lyrics:
Do you ever feel
Like a plastic bag

2
00:00:18,020 --> 00:00:22,140
Drifting through the wind
Wanting to start again

3
00:00:22,140 --> 00:00:25,440
Do you ever feel
Feel so paper-thin

4
00:00:25,440 --> 00:00:29,640
Like a house of cards
One blow from caving in

5
00:00:29,640 --> 00:00:33,400
Do you ever feel
Already buried deep

[...]
==========
```
