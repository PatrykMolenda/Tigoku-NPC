package tigoku.npc.storage;

import tigoku.npc.model.NpcDefinition;

import java.io.IOException;
import java.util.Map;

public interface NpcRepository {
    Map<String, NpcDefinition> loadAll() throws IOException;
    void saveAll(Map<String, NpcDefinition> npcs) throws IOException;
}

