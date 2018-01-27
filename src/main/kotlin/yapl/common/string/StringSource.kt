package yapl.common.string

import yapl.common.Reader
import yapl.common.Source

class StringSource(val text: String) : Source {
    override fun createReader() = StringReader(this)

    override fun toString() = text
}
