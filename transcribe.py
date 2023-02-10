import sys
import whisper
from whisper.utils import WriteSRT

modelSize = "large"

if __name__ == "__main__":
    mp3 = sys.argv[1] #"data/KatyPerry-Firework.mp3"
    if len(sys.argv) >= 2 :
        modelSize = sys.argv[2]
    
    print("Loading Whisper model...")
    model = whisper.load_model(modelSize)
    
    print("Transcribing: "+mp3+" ...")
    result = model.transcribe(mp3)
    
    print(result["text"])
    
    print("Saving: "+mp3+".srt ...")
    writer = WriteSRT("data/")
    writer(result, mp3)

