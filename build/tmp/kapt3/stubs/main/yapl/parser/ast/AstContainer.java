package yapl.parser.ast;

@kotlin.Metadata(mv = {1, 1, 9}, bv = {1, 0, 2}, k = 1, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0003\b&\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002R\u0018\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00010\u0004X\u00a6\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0005\u0010\u0006\u00a8\u0006\u0007"}, d2 = {"Lyapl/parser/ast/AstContainer;", "Lyapl/parser/ast/AstEntry;", "()V", "children", "", "getChildren", "()Ljava/util/List;", "yapl"})
public abstract class AstContainer extends yapl.parser.ast.AstEntry {
    
    @org.jetbrains.annotations.NotNull()
    public abstract java.util.List<yapl.parser.ast.AstEntry> getChildren();
    
    public AstContainer() {
        super();
    }
}