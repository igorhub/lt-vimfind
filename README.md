lt-vimfind
----------

lt-vimfind is a Light Table plugin that make 'find' command
behave like in vim. The plugin is in its early stage, expect
rough edges.

## Usage

lt-vimfind provides two commands, `search` and `find-next`. By
default, they are bound to the keys you would expect (see
[lt_vimfind.keymap](https://github.com/igorhub/lt-vimfind/blob/master/lt_vimfind.keymap)):

```clojure
{:editor.keys.vim.normal {"/" [(:lt.plugins.lt-vimfind/search :forward)]
                          "?" [(:lt.plugins.lt-vimfind/search :backward)]
                          "n" [(:lt.plugins.lt-vimfind/find-next :forward)]
                          "N" [(:lt.plugins.lt-vimfind/find-next :backward)]}}
```

## Community

Join the discussion at the [Light Table newsgroup](https://groups.google.com/forum/#!topic/light-table-discussion/j-QzgCQttG0).
