package yapl.parser.ast

data class AstVariantType(val variants: AstNodeList<AstTypeReference>) : AstTypeReference()
