package yapl.common;

@kotlin.Metadata(mv = {1, 1, 9}, bv = {1, 0, 2}, k = 1, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\u0010\u000b\n\u0000\bf\u0018\u00002\u00020\u0001J\b\u0010\f\u001a\u00020\rH&J\u0012\u0010\f\u001a\u00020\r2\b\b\u0002\u0010\b\u001a\u00020\u000eH&R\u0012\u0010\u0002\u001a\u00020\u0003X\u00a6\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0004\u0010\u0005R\u0012\u0010\u0006\u001a\u00020\u0003X\u00a6\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0007\u0010\u0005R\u0014\u0010\b\u001a\u0004\u0018\u00010\tX\u00a6\u0004\u00a2\u0006\u0006\u001a\u0004\b\n\u0010\u000b\u00a8\u0006\u000f"}, d2 = {"Lyapl/common/Position;", "", "column", "", "getColumn", "()I", "line", "getLine", "source", "Lyapl/common/Source;", "getSource", "()Lyapl/common/Source;", "toString", "", "", "yapl"})
public abstract interface Position {
    
    @org.jetbrains.annotations.Nullable()
    public abstract yapl.common.Source getSource();
    
    public abstract int getColumn();
    
    public abstract int getLine();
    
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public abstract java.lang.String toString();
    
    @org.jetbrains.annotations.NotNull()
    public abstract java.lang.String toString(boolean source);
    
    @kotlin.Metadata(mv = {1, 1, 9}, bv = {1, 0, 2}, k = 3)
    public final class DefaultImpls {
    }
}