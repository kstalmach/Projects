#ifndef WAVFILE_H
#define WAVFILE_H

#include <algorithm>
#include <cstdint>
#include <cstring> 
#include <fstream>
#include <iomanip>
#include <iostream>
#include <sstream>
#include <stdexcept>
#include <string>
#include <unordered_map>
#include <vector>

constexpr char DEFAULT_INPUT_WAV[] = "test.wav";
constexpr char DEFAULT_CSV_FILE[] = "audio_data.csv";
constexpr char DEFAULT_OUTPUT_WAV[] = "output.wav";
constexpr char HEADER_FILE[] = "header_info.txt";

#pragma pack(push, 1)
struct WavHeader {
    char riff[4];
    uint32_t overall_size;
    char wave[4];
    char fmt_chunk_marker[4];
    uint32_t length_of_fmt;
    uint16_t format_type;
    uint16_t channels;
    uint32_t sample_rate;
    uint32_t byterate;
    uint16_t block_align;
    uint16_t bits_per_sample;
    char data_chunk_header[4];
    uint32_t data_size;
};
#pragma pack(pop)

class WavFile {
private:
    std::ifstream file;
    WavHeader header;
    std::vector<int16_t> audioData;
    std::string filename;

    bool isValid() const;

public:
    WavFile(const std::string& filename = DEFAULT_INPUT_WAV);

    void displayInfo() const;
    void readAudioData();
    void displaySamples(size_t count = 10) const;
    void normalizeAudio();

    void saveHeaderToFile(const std::string& outputFilename = HEADER_FILE) const;
    void exportToCSV(const std::string& outputFilename = DEFAULT_CSV_FILE) const;
    void importFromCSV(const std::string& inputFilename = DEFAULT_CSV_FILE);
    void saveToWav(const std::string& outputFilename = DEFAULT_OUTPUT_WAV) const;
    

    uint32_t getSampleRate() const { return header.sample_rate; }
    std::string getFileInfo() const;
    std::string getFormatName() const;

    ~WavFile();
};

#endif // WAVFILE_H
