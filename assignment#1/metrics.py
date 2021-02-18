import sys
import json
import pandas as pd
import numpy as np
from trie import Trie

if __name__ != '__main__':
    sys.exit()

layout_path = ""
dict_path = ""


def parse_argv():
    global layout_path
    global dict_path
    if len(sys.argv) != 3:
        print('usage: python metrics.py [path/to/layout] [path/to/dictionary]')
    layout_path = sys.argv[1]
    dict_path = sys.argv[2]


def parse_layout(path):
    layout_str = ""
    with open(layout_path) as f:
        layout_str = f.read()
    layout = json.loads(layout_str)
    return layout


def get_file_name(path):
    return path.split('/')[-1]


class KeyboardLayout:

    def __init__(self, dict_):
        self._dict = dict_
        self._depths = dict()
        for key, chars in self._dict.items():
            for index, c in enumerate(chars):
                self._depths[c] = index + 1

    def char_depth(self, char):
        """How much taps to perform in order to select specified character"""
        return self._depths[char]


def keystrokes_per_word(trie, layout, word, line):
    keystrokes = 0
    prediction_threshold = trie.keystrokes_per_word(word, line)
    for index, c in enumerate(word):
        if index + 1 > prediction_threshold:
            break
        keystrokes += layout.char_depth(c)
    return keystrokes


def kspc(trie, layout, dict_):
    dict_.insert(0, column='#', value=range(0, dict_.shape[0]))
    dict_['Kw'] = dict_['#'].transform(lambda x : keystrokes_per_word(trie, layout, dict_['Word'][x], x) + 1)
    dict_['Cw'] = dict_['Word'].transform(lambda w : len(w))
    kspc = (dict_['Kw'] * dict_['Freq']).sum() / (dict_['Cw'] * dict_["Freq"]).sum()
    return kspc

def lp(trie, layout, dict_):
    return None

def compute_metrics(trie, layout, dict_):
    df = pd.DataFrame(columns=['kspc', 'lp'])
    df.loc[get_file_name(layout_path)] = [
        kspc(trie, layout, dict_), lp(trie, layout, dict_)]
    return df

parse_argv()
layout = KeyboardLayout(parse_layout(layout_path))
dict_ = pd.read_csv(dict_path)

trie = Trie()
trie.add_dataset(dict_)

metrics = compute_metrics(trie, layout, dict_)
print(metrics.to_string())
