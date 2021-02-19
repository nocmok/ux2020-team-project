import sys
import json
import pandas as pd
import numpy as np
from collections import namedtuple
from trie import Trie

import time

if __name__ != '__main__':
    sys.exit()

layout_pathes = []
baseline_layout_path = ""
dict_path = ""
baseline_layout = None

chars = ['а', 'б', 'в', 'г', 'д', 'е', 'ж', 'з', 'и', 'й', 'к', 'л', 'м', 'н', 'о',
         'п', 'р', 'с', 'т', 'у', 'ф', 'х', 'ц', 'ч', 'ш', 'щ', 'ъ', "ы", 'ь', 'э', 'ю', 'я']

Point = namedtuple('Point', ['x', 'y'])

to_point = {
    "q": Point(0, 0),
    "w": Point(1, 0),
    "e": Point(2, 0),
    "r": Point(3, 0),
    "t": Point(4, 0),
    "y": Point(5, 0),
    "u": Point(6, 0),
    "i": Point(7, 0),
    "o": Point(8, 0),
    "p": Point(9, 0),
    "[": Point(10, 0),
    "]": Point(11, 0),
    "a": Point(0, 1),
    "s": Point(1, 1),
    "d": Point(2, 1),
    "f": Point(3, 1),
    "g": Point(4, 1),
    "h": Point(5, 1),
    "j": Point(6, 1),
    "k": Point(7, 1),
    "l": Point(8, 1),
    ";": Point(9, 1),
    "'": Point(10, 1),
    "z": Point(0, 2),
    "x": Point(1, 2),
    "c": Point(2, 2),
    "v": Point(3, 2),
    "b": Point(4, 2),
    "n": Point(5, 2),
    "m": Point(6, 2),
    ",": Point(7, 2),
    ".": Point(8, 2),
    "/": Point(9, 2),
}


def parse_argv():
    global layout_pathes
    global dict_path
    global baseline_layout_path

    if len(sys.argv) < 4:
        print(
            'usage: python metrics.py [path/to/layout#1, ...] [path/to/baseline/layout] [path/to/dictionary]')

    layout_pathes = [path for path in sys.argv[1:-2]]
    # for i in range(1, 29):
    # layout_pathes.append(f'keyboard_prefix_{i}.json')

    baseline_layout_path = sys.argv[-2]
    dict_path = sys.argv[-1]


def parse_layout(path):
    layout_str = ""
    with open(path) as f:
        layout_str = f.read()
    layout = json.loads(layout_str)
    return layout


def get_file_name(path):
    return path.split('/')[-1]


class KeyboardLayout:

    def __init__(self, dict_):
        self._dict = dict_
        self._depths = dict()
        self._to_key = dict()
        for key, chars in self._dict.items():
            for index, c in enumerate(chars):
                self._depths[c] = index + 1
                self._to_key[c] = key

    def char_key(self, char):
        return self._to_key[char]

    def char_depth(self, char):
        """How much taps to perform in order to select specified character"""
        return self._depths[char]


def kspw_exclude_space(trie, layout, word, line):
    ks = 0
    predict_threshold = trie.keystrokes_per_word(word, line)
    for i, c in enumerate(word):
        if i + 1 > predict_threshold:
            break
        ks += layout.char_depth(c)
    return ks


def kspw(trie, layout, word, line):
    ks = 0
    predict_threshold = trie.keystrokes_per_word(word, line)
    for i, c in enumerate(word):
        if i + 1 > predict_threshold:
            break
        ks += layout.char_depth(c)
    return ks + 1 if predict_threshold < len(word) else ks


def kspc_exclude_space(trie, layout, dict_):
    kw = np.array(list(map(lambda i: kspw_exclude_space(
        trie, layout, dict_['Word'].values[i], i), dict_['#'].values)))
    cw = np.array(list(map(lambda w: len(w), dict_['Word'].values)))
    kspc = (kw * dict_['Freq'].values).sum() / \
        (cw * dict_['Freq'].values).sum()
    return kspc


def kspc(trie, layout, dict_):
    kw = np.array(list(map(lambda i: kspw(trie, layout, dict_[
                  'Word'].values[i], i), dict_['#'].values)))
    cw = np.array(list(map(lambda w: len(w), dict_['Word'].values)))
    kspc = (kw * dict_['Freq'].values).sum() / \
        (cw * dict_['Freq'].values).sum()
    return kspc


def lp(trie, layout, dict_):
    lp = 0
    for char in chars:
        lp += abs(to_point[layout.char_key(char)].x -
                  to_point[baseline_layout.char_key(char)].x)
        lp += abs(to_point[layout.char_key(char)].y -
                  to_point[baseline_layout.char_key(char)].y)
    return lp


kspc_exclude_space.metric_name = 'kspc(exclude space)'
kspc.metric_name = 'kspc'
lp.metric_name = 'lp'


def compute_metrics(trie, layouts, dict_):
    metrics = [kspc_exclude_space, kspc, lp]
    df = pd.DataFrame(columns=[metric.metric_name for metric in metrics])

    for i in range(0, len(layouts)):
        df.loc[get_file_name(layout_pathes[i])] = [metric(
            trie, layouts[i], dict_) for metric in metrics]
    return df


parse_argv()
layouts = [KeyboardLayout(parse_layout(path)) for path in layout_pathes]
baseline_layout = KeyboardLayout(parse_layout(baseline_layout_path))

dict_ = pd.read_csv(dict_path)
dict_.insert(0, column='#', value=range(0, dict_.shape[0]))

trie = Trie()
trie.add_dataset(dict_)
metrics = compute_metrics(trie, layouts, dict_)

print(metrics.to_string())
