package yapl.common;

@kotlin.Metadata(mv = {1, 1, 9}, bv = {1, 0, 2}, k = 1, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\f\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\bf\u0018\u00002\u00020\u0001J\b\u0010\u0006\u001a\u00020\u0007H\u0016J\b\u0010\b\u001a\u00020\u0007H\u0016J\b\u0010\t\u001a\u00020\nH&J\b\u0010\u000b\u001a\u00020\nH&J\b\u0010\f\u001a\u00020\rH&R\u0012\u0010\u0002\u001a\u00020\u0003X\u00a6\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0004\u0010\u0005\u00a8\u0006\u000e"}, d2 = {"Lyapl/common/Reader;", "", "source", "Lyapl/common/Source;", "getSource", "()Lyapl/common/Source;", "eof", "", "eol", "next", "", "peek", "position", "Lyapl/common/Position;", "yapl"})
public abstract interface Reader {
    
    @org.jetbrains.annotations.NotNull()
    public abstract yapl.common.Source getSource();
    
    @org.jetbrains.annotations.NotNull()
    public abstract yapl.common.Position position();
    
    public abstract char peek();
    
    public abstract char next();
    
    public abstract boolean eof();
    
    public abstract boolean eol();
    
    @kotlin.Metadata(mv = {1, 1, 9}, bv = {1, 0, 2}, k = 3)
    public final class DefaultImpls {
        
        public static boolean eof(yapl.common.Reader $this) {
            return false;
        }
        
        public static boolean eol(yapl.common.Reader $this) {
            return false;
        }
    }
}