#include "WavFile.h"
#include "utils.h"

int main(int argc, char* argv[]) {
    try {
        printSeparator();
        
        std::string inputFile = (argc > 1) ? argv[1] : DEFAULT_INPUT_WAV;
        std::string outputFile = (argc > 2) ? argv[2] : DEFAULT_OUTPUT_WAV;

        saveLog("Starting file analysis: " + inputFile);

        WavFile wav(inputFile);
        wav.displayInfo();
        wav.displaySamples();
        wav.normalizeAudio();
        wav.saveHeaderToFile();
        wav.exportToCSV();
        
        // wav.importFromCSV();
        // wav.saveToWav(outputFile);

        saveLog("File analysis completed successfully. Output saved to: " + outputFile);
        printSeparator();
    } catch (const std::exception& e) {
        logError("Error: " + std::string(e.what()));
        std::cerr << "Error: " << e.what() << std::endl;
    }
    return 0;
}
