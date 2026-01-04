package abeshutt.staracademy.live.api.dto;

import com.google.common.hash.Hashing;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Hash {

    private final Algorithm algorithm;
    private final byte[] value;

    private Hash(Algorithm algorithm, byte[] value) {
        this.algorithm = algorithm;
        this.value = value;
    }

    public static Hash of(Algorithm algorithm, byte[] value) {
        return new Hash(algorithm, value);
    }

    public Algorithm getAlgorithm() {
        return this.algorithm;
    }

    public byte[] getValue() {
        return this.value;
    }

    public enum Algorithm {
        MURMUR3_32("murmur3_32") {
            @Override
            public byte[] hash(byte[] input) {
                int result = Hashing.murmur3_32_fixed().hashBytes(input).asInt();
                return new byte[] { (byte)(result >>> 24), (byte)(result >>> 16), (byte)(result >>> 8), (byte)result };
            }
        };

        private static final Map<String, Algorithm> ID_TO_VALUE = Arrays.stream(Algorithm.values())
                .collect(Collectors.toUnmodifiableMap(Algorithm::getId, Function.identity()));

        private final String id;

        Algorithm(String id) {
            this.id = id;
        }

        public String getId() {
            return this.id;
        }

        public abstract byte[] hash(byte[] input);

        public static Optional<Algorithm> fromId(String id) {
            return Optional.ofNullable(ID_TO_VALUE.get(id));
        }
    }

}
