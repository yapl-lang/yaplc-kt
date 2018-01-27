package yapl.parser.token;

@kotlin.Metadata(mv = {1, 1, 9}, bv = {1, 0, 2}, k = 1, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\f\n\u0002\b\t\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0086\b\u0018\u0000 \u00162\u00020\u0001:\u0001\u0016B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\t\u0010\u000b\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\f\u001a\u00020\u0005H\u00c6\u0003J\u001d\u0010\r\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005H\u00c6\u0001J\u0013\u0010\u000e\u001a\u00020\u000f2\b\u0010\u0010\u001a\u0004\u0018\u00010\u0011H\u00d6\u0003J\t\u0010\u0012\u001a\u00020\u0013H\u00d6\u0001J\t\u0010\u0014\u001a\u00020\u0015H\u00d6\u0001R\u0014\u0010\u0002\u001a\u00020\u0003X\u0096\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\n\u00a8\u0006\u0017"}, d2 = {"Lyapl/parser/token/TokenPunctuation;", "Lyapl/parser/token/Token;", "bound", "Lyapl/common/Bound;", "value", "", "(Lyapl/common/Bound;C)V", "getBound", "()Lyapl/common/Bound;", "getValue", "()C", "component1", "component2", "copy", "equals", "", "other", "", "hashCode", "", "toString", "", "Companion", "yapl"})
public final class TokenPunctuation extends yapl.parser.token.Token {
    @org.jetbrains.annotations.NotNull()
    private final yapl.common.Bound bound = null;
    private final char value = '\u0000';
    @org.jetbrains.annotations.NotNull()
    private static final java.util.List<java.lang.Character> Punctuations = null;
    public static final yapl.parser.token.TokenPunctuation.Companion Companion = null;
    
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public yapl.common.Bound getBound() {
        return null;
    }
    
    public final char getValue() {
        return '\u0000';
    }
    
    public TokenPunctuation(@org.jetbrains.annotations.NotNull()
    yapl.common.Bound bound, char value) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final yapl.common.Bound component1() {
        return null;
    }
    
    public final char component2() {
        return '\u0000';
    }
    
    @org.jetbrains.annotations.NotNull()
    public final yapl.parser.token.TokenPunctuation copy(@org.jetbrains.annotations.NotNull()
    yapl.common.Bound bound, char value) {
        return null;
    }
    
    @java.lang.Override()
    public java.lang.String toString() {
        return null;
    }
    
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    @java.lang.Override()
    public boolean equals(java.lang.Object p0) {
        return false;
    }
    
    @kotlin.Metadata(mv = {1, 1, 9}, bv = {1, 0, 2}, k = 1, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0010\f\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u0017\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007\u00a8\u0006\b"}, d2 = {"Lyapl/parser/token/TokenPunctuation$Companion;", "", "()V", "Punctuations", "", "", "getPunctuations", "()Ljava/util/List;", "yapl"})
    public static final class Companion {
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<java.lang.Character> getPunctuations() {
            return null;
        }
        
        private Companion() {
            super();
        }
    }
}