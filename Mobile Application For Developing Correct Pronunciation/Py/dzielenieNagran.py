from pydub import AudioSegment
from pydub.silence import split_on_silence
import os

source_folder = "Całe Nagrania"
destination_folder = "AudioFiles"

recording_number = 31

file_mapping = {
    "szafa": "1_szafa",
    "szopa": "2_szopa",
    "szelki": "3_szelki",
    "kasza": "4_kasza",
    "nosze": "5_nosze",
    "wieszak": "6_wieszak",
    "kosz": "7_kosz",
    "afisz": "8_afisz",
    "gulasz": "9_gulasz"
}

input_filename = f"{recording_number}.wav"
input_filepath = os.path.join(source_folder, input_filename)

audio = AudioSegment.from_file(input_filepath)

chunks = split_on_silence(
    audio,
    min_silence_len=450,  
    silence_thresh=-36 
)

if len(chunks) != 9:
    print(f"Podzielono na {len(chunks)} części.")
    raise ValueError("Nagranie musi być podzielone na dokładnie 9 części.")

for i, (name, folder) in enumerate(file_mapping.items()):
    chunk = chunks[i]
    out_file = f"{recording_number}-{name}.wav"
    out_filepath = os.path.join(destination_folder, folder, out_file)
    chunk.export(out_filepath, format="wav")
    print(f"Saved {out_filepath}")