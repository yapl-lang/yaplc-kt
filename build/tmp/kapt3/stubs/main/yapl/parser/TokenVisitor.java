package yapl.parser;

@kotlin.Metadata(mv = {1, 1, 9}, bv = {1, 0, 2}, k = 1, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\bf\u0018\u00002\u00020\u0001J\f\u0010\u0002\u001a\u00020\u0003*\u00020\u0004H\u0016J\f\u0010\u0002\u001a\u00020\u0003*\u00020\u0005H\u0016J\f\u0010\u0002\u001a\u00020\u0003*\u00020\u0006H\u0016J\f\u0010\u0002\u001a\u00020\u0003*\u00020\u0007H\u0016J\f\u0010\u0002\u001a\u00020\u0003*\u00020\bH\u0016J\f\u0010\u0002\u001a\u00020\u0003*\u00020\tH\u0016J\f\u0010\u0002\u001a\u00020\u0003*\u00020\nH\u0016J\f\u0010\u0002\u001a\u00020\u0003*\u00020\u000bH\u0016J\f\u0010\u0002\u001a\u00020\u0003*\u00020\fH\u0016\u00a8\u0006\r"}, d2 = {"Lyapl/parser/TokenVisitor;", "", "visit", "", "Lyapl/parser/token/TokenError;", "Lyapl/parser/token/TokenIdentifier;", "Lyapl/parser/token/TokenKeyword;", "Lyapl/parser/token/TokenLineComment;", "Lyapl/parser/token/TokenNewline;", "Lyapl/parser/token/TokenOptionalKeyword;", "Lyapl/parser/token/TokenPunctuation;", "Lyapl/parser/token/TokenSemicolon;", "Lyapl/parser/token/TokenWhitespace;", "yapl"})
public abstract interface TokenVisitor {
    
    public abstract void visit(@org.jetbrains.annotations.NotNull()
    yapl.parser.token.TokenError $receiver);
    
    public abstract void visit(@org.jetbrains.annotations.NotNull()
    yapl.parser.token.TokenIdentifier $receiver);
    
    public abstract void visit(@org.jetbrains.annotations.NotNull()
    yapl.parser.token.TokenKeyword $receiver);
    
    public abstract void visit(@org.jetbrains.annotations.NotNull()
    yapl.parser.token.TokenLineComment $receiver);
    
    public abstract void visit(@org.jetbrains.annotations.NotNull()
    yapl.parser.token.TokenNewline $receiver);
    
    public abstract void visit(@org.jetbrains.annotations.NotNull()
    yapl.parser.token.TokenOptionalKeyword $receiver);
    
    public abstract void visit(@org.jetbrains.annotations.NotNull()
    yapl.parser.token.TokenPunctuation $receiver);
    
    public abstract void visit(@org.jetbrains.annotations.NotNull()
    yapl.parser.token.TokenSemicolon $receiver);
    
    public abstract void visit(@org.jetbrains.annotations.NotNull()
    yapl.parser.token.TokenWhitespace $receiver);
    
    @kotlin.Metadata(mv = {1, 1, 9}, bv = {1, 0, 2}, k = 3)
    public final class DefaultImpls {
        
        public static void visit(@org.jetbrains.annotations.NotNull()
        yapl.parser.TokenVisitor $this, yapl.parser.token.TokenError $receiver) {
        }
        
        public static void visit(@org.jetbrains.annotations.NotNull()
        yapl.parser.TokenVisitor $this, yapl.parser.token.TokenIdentifier $receiver) {
        }
        
        public static void visit(@org.jetbrains.annotations.NotNull()
        yapl.parser.TokenVisitor $this, yapl.parser.token.TokenKeyword $receiver) {
        }
        
        public static void visit(@org.jetbrains.annotations.NotNull()
        yapl.parser.TokenVisitor $this, yapl.parser.token.TokenLineComment $receiver) {
        }
        
        public static void visit(@org.jetbrains.annotations.NotNull()
        yapl.parser.TokenVisitor $this, yapl.parser.token.TokenNewline $receiver) {
        }
        
        public static void visit(@org.jetbrains.annotations.NotNull()
        yapl.parser.TokenVisitor $this, yapl.parser.token.TokenOptionalKeyword $receiver) {
        }
        
        public static void visit(@org.jetbrains.annotations.NotNull()
        yapl.parser.TokenVisitor $this, yapl.parser.token.TokenPunctuation $receiver) {
        }
        
        public static void visit(@org.jetbrains.annotations.NotNull()
        yapl.parser.TokenVisitor $this, yapl.parser.token.TokenSemicolon $receiver) {
        }
        
        public static void visit(@org.jetbrains.annotations.NotNull()
        yapl.parser.TokenVisitor $this, yapl.parser.token.TokenWhitespace $receiver) {
        }
    }
}