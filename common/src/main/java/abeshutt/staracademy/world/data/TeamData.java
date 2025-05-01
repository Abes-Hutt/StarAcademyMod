package abeshutt.staracademy.world.data;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

import java.util.*;

public class TeamData extends WorldData {

    private final Map<UUID, AcademyTeam> teams;

    public TeamData() {
        this.teams = new LinkedHashMap<>();
    }

    @Override
    public Optional<NbtCompound> writeNbt() {
        return Optional.of(new NbtCompound()).map(nbt -> {
            NbtList teams = new NbtList();

            for(AcademyTeam team : this.teams.values()) {
               team.writeNbt().ifPresent(teams::add);
            }

            nbt.put("teams", teams);
            return nbt;
        });
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        NbtList teams = nbt.getList("teams", NbtElement.COMPOUND_TYPE);

        for(int i = 0; i < teams.size(); i++) {
            AcademyTeam team = new AcademyTeam();
            team.readNbt(teams.getCompound(i));
            this.teams.put(team.getUuid(), team);
        }
    }

}
