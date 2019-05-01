# EC504-Language-Correction

## Requirements

1. Design and complement a Crawler that stores information about at least 1TB of English text on the Internet in a database of at most 1GB. 

2. Design and implement a Checker that is able to assign confidence to the various language structures in a sample English text, identifying unusual outliers. 

3. Provide convincing experimental evidence of the effectiveness of the checker (say, from a comparison to third-party tools or manually evaluated samples). 

4. Build a Command-line User Interface that allows users to check a given file, and output an ordered list of the most suspicious phrases (most suspicious to least suspicious), using the following usage: 

checker -file [file to check] . 

The output should be a list of suspicious phrases and suspicion level (0-100), one per line, from most suspicious to least suspicious, For example, one line could be: 

"strange so choice" 70
