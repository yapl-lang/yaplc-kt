package yapl.common.string;

@kotlin.Metadata(mv = {1, 1, 9}, bv = {1, 0, 2}, k = 1, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0010\f\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\u000f\b\u0016\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\bJ\b\u0010\u0011\u001a\u00020\u0012H\u0016J\b\u0010\u0013\u001a\u00020\u0012H\u0016J\b\u0010\u0014\u001a\u00020\nH\u0016R\u0010\u0010\t\u001a\u0004\u0018\u00010\nX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0002\u001a\u00020\u0005X\u0096\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0015"}, d2 = {"Lyapl/common/string/StringReader;", "Lyapl/common/Reader;", "source", "Lyapl/common/string/StringSource;", "(Lyapl/common/string/StringSource;)V", "Lyapl/common/Source;", "string", "", "(Lyapl/common/Source;Ljava/lang/String;)V", "cachedPosition", "Lyapl/common/string/StringPosition;", "column", "", "line", "offset", "getSource", "()Lyapl/common/Source;", "next", "", "peek", "position", "yapl"})
public final class StringReader implements yapl.common.Reader {
    private int offset;
    private int line;
    private int column;
    private yapl.common.string.StringPosition cachedPosition;
    @org.jetbrains.annotations.NotNull()
    private final yapl.common.Source source = null;
    private final java.lang.String string = null;
    
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public yapl.common.string.StringPosition position() {
        return null;
    }
    
    @java.lang.Override()
    public char peek() {
        return '\u0000';
    }
    
    @java.lang.Override()
    public char next() {
        return '\u0000';
    }
    
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public yapl.common.Source getSource() {
        return null;
    }
    
    public StringReader(@org.jetbrains.annotations.NotNull()
    yapl.common.Source source, @org.jetbrains.annotations.NotNull()
    java.lang.String string) {
        super();
    }
    
    public StringReader(@org.jetbrains.annotations.NotNull()
    yapl.common.string.StringSource source) {
        super();
    }
    
    public boolean eof() {
        return false;
    }
    
    public boolean eol() {
        return false;
    }
}