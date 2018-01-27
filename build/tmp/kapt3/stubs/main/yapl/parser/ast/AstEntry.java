package yapl.parser.ast;

@kotlin.Metadata(mv = {1, 1, 9}, bv = {1, 0, 2}, k = 1, d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0003\b&\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002R\u0012\u0010\u0003\u001a\u00020\u0004X\u00a6\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0005\u0010\u0006R\u0013\u0010\u0007\u001a\u0004\u0018\u00010\b8F\u00a2\u0006\u0006\u001a\u0004\b\t\u0010\n\u00a8\u0006\u000b"}, d2 = {"Lyapl/parser/ast/AstEntry;", "", "()V", "bound", "Lyapl/common/Bound;", "getBound", "()Lyapl/common/Bound;", "typeName", "", "getTypeName", "()Ljava/lang/String;", "yapl"})
public abstract class AstEntry {
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getTypeName() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public abstract yapl.common.Bound getBound();
    
    public AstEntry() {
        super();
    }
}