import sys
import numpy as np
import pandas as pd
import re

dict_path = ""
output_path = ""

if __name__ != "__main__":
    sys.exit()


def parse_argv():
    global dict_path
    global output_path
    if(len(sys.argv) != 3):
        print(
            'usage: python cleanup.py [path/to/dictionary] [output/file/path]')
        sys.exit()
    dict_path = sys.argv[1]
    output_path = sys.argv[2]

cyr_pattern_str = "^[абвгдежзийклмнопрстуфхцчшщъыьэюя]+$" 
cyr_pattern = re.compile(cyr_pattern_str)

def drop_word(word):
    return cyr_pattern.match(word) is None

def cleanup():
    dict_ = pd.read_table(dict_path, names=['Word', 'Freq', 'FreqPM'])
    rows_before = dict_.size
    dict_ = dict_[~dict_['Word'].transform(lambda w : drop_word(w))]
    rows_after = dict_.size
    print(f'{rows_before - rows_after} rows dropped')
    dict_.to_csv(output_path, index=False)

parse_argv()
cleanup()