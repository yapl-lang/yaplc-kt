package yapl.parser

import yapl.common.Bound

class ParseError(override val message: String, val bound: Bound) : RuntimeException()
