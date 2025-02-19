from pydub import AudioSegment
import os

folder_path = "Całe Nagrania"

for file in os.listdir(folder_path):
    print(file)
    if file.endswith(".m4a"):  
        file_path = os.path.join(folder_path, file)
        sound = AudioSegment.from_file(file_path, format="m4a")
        output_path = os.path.join(folder_path, f"{file[:-4]}.wav")  
        sound.export(output_path, format="wav")
        print(f"Converted {file} to wav")
        
    

for file in os.listdir(folder_path):
    print(file)
    if file.endswith(".wav"): 
        file_path = os.path.join(folder_path, file)
        sound = AudioSegment.from_file(file_path, format="wav")
        if sound.channels == 2:
            sound = sound.set_channels(1)
            sound.export(file_path, format="wav")
            print(f"Converted {file} to mono")
        else:
            print(f"{file} is already mono")
            