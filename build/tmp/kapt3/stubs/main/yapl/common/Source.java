package yapl.common;

@kotlin.Metadata(mv = {1, 1, 9}, bv = {1, 0, 2}, k = 1, d1 = {"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\bf\u0018\u00002\u00020\u0001J\b\u0010\u0002\u001a\u00020\u0003H&J\b\u0010\u0004\u001a\u00020\u0005H&\u00a8\u0006\u0006"}, d2 = {"Lyapl/common/Source;", "", "createReader", "Lyapl/common/Reader;", "toString", "", "yapl"})
public abstract interface Source {
    
    @org.jetbrains.annotations.NotNull()
    public abstract yapl.common.Reader createReader();
    
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public abstract java.lang.String toString();
}