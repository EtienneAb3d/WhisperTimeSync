import whisper
from whisper.utils import WriteSRT

mp3 = "data/KatyPerry-Firework.mp3"

print("Loading Whisper model...")
model = whisper.load_model("large")

print("Transcribing: "+mp3+" ...")
result = model.transcribe(mp3)

print(result["text"])

print("Saving: "+mp3+".srt ...")
writer = WriteSRT("data/")
writer(result, mp3)

