package io.klovers.server.common.papago;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PapagoResponse {
    private PapagoMessage message;

    @Getter
    public static class PapagoMessage {
        private PapagoResult result;

        @Getter
        public static class PapagoResult {
            private String translatedText;

            public void setTranslatedText(String translatedText) {
                this.translatedText = translatedText;
            }
        }
    }
}
