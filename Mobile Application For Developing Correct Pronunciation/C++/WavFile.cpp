#include "WavFile.h"

WavFile::WavFile(const std::string& filename) : filename(filename) {
    file.open(filename, std::ios::binary);
    if (!file) {
        throw std::runtime_error("Unable to open file: " + filename);
    }

    file.read(reinterpret_cast<char*>(&header), sizeof(WavHeader));
    if (!isValid()) {
        throw std::runtime_error("Invalid WAV format: " + filename);
    }

    readAudioData();
}

bool WavFile::isValid() const {
    return std::string(header.riff, 4) == "RIFF" && std::string(header.wave, 4) == "WAVE";
}

void WavFile::readAudioData() {
    if (!file.is_open()) return;

    std::vector<char> buffer(header.data_size);
    file.read(buffer.data(), header.data_size);

    audioData.resize(header.data_size / (header.bits_per_sample / 8));
    std::memcpy(audioData.data(), buffer.data(), buffer.size());
}

void WavFile::displayInfo() const {
    std::cout << getFileInfo();
}

void WavFile::displaySamples(size_t count) const {
    for (size_t i = 0; i < count && i < audioData.size(); i++) {
        std::cout << "[" << i << "]: " << audioData[i] << std::endl;
    }
}

void WavFile::normalizeAudio() {
    if (audioData.empty()) return;

    int16_t maxValue = *std::max_element(audioData.begin(), audioData.end());
    if (maxValue == 0) return;

    std::transform(audioData.begin(), audioData.end(), audioData.begin(),
                   [maxValue](int16_t sample) {
                       return static_cast<int16_t>((sample / static_cast<float>(maxValue)) * 32767);
                   });
}

void WavFile::saveHeaderToFile(const std::string& outputFilename) const {
    std::ofstream outFile(outputFilename);
    if (!outFile) {
        throw std::runtime_error("Unable to save header to file: " + outputFilename);
    }

    outFile << getFileInfo();
}

void WavFile::exportToCSV(const std::string& outputFilename) const {
    std::ofstream outFile(outputFilename);
    if (!outFile) {
        throw std::runtime_error("Unable to open CSV file for writing: " + outputFilename);
    }

    outFile << "Index, Sample Value\n";
    for (size_t i = 0; i < audioData.size(); ++i) {
        outFile << i << "," << audioData[i] << "\n";
    }
}

void WavFile::importFromCSV(const std::string& inputFilename) {
    std::ifstream inFile(inputFilename);
    if (!inFile) {
        throw std::runtime_error("Unable to open CSV file: " + inputFilename);
    }

    std::string line;
    audioData.clear();

    std::getline(inFile, line); 

    while (std::getline(inFile, line)) {
        std::stringstream ss(line);
        std::string index, value;
        
        std::getline(ss, index, ',');
        std::getline(ss, value, ',');

        try {
            audioData.push_back(std::stoi(value));
        } catch (const std::exception&) {
            std::cerr << "Error converting CSV value: " << value << std::endl;
        }
    }
}

void WavFile::saveToWav(const std::string& outputFilename) const {
    if (audioData.empty()) {
        throw std::runtime_error("No audio data to save!");
    }

    std::ofstream outFile(outputFilename, std::ios::binary);
    if (!outFile) {
        throw std::runtime_error("Unable to create WAV file: " + outputFilename);
    }

    WavHeader newHeader = header;
    newHeader.data_size = audioData.size() * sizeof(int16_t);
    newHeader.overall_size = sizeof(WavHeader) - 8 + newHeader.data_size;
    newHeader.block_align = newHeader.channels * (newHeader.bits_per_sample / 8);
    newHeader.byterate = newHeader.sample_rate * newHeader.block_align;

    outFile.write(reinterpret_cast<const char*>(&newHeader), sizeof(WavHeader));
    outFile.write(reinterpret_cast<const char*>(audioData.data()), newHeader.data_size);
}

std::string WavFile::getFileInfo() const {
    std::ostringstream info;
    info << "File: " << filename << "\n"
         << "Format: " << getFormatName() << "\n"
         << "Channels: " << header.channels << "\n"
         << "Sample rate: " << header.sample_rate << " Hz\n"
         << "Bit depth: " << header.bits_per_sample << " bits\n"
         << "Audio data size: " << header.data_size << " bytes\n";
    return info.str();
}

std::string WavFile::getFormatName() const {
    static const std::unordered_map<uint16_t, std::string> formatMap = {
        {1, "PCM (Uncompressed)"},
        {3, "IEEE Float"},
        {6, "A-law"},
        {7, "Mu-law"},
        {0xFFFE, "Extensible (Determined by SubFormat GUID)"} 
    };

    auto it = formatMap.find(header.format_type);
    return (it != formatMap.end()) ? it->second : "Unknown Format";
}

WavFile::~WavFile() {
    if (file.is_open()) {
        file.close();
    }
}
