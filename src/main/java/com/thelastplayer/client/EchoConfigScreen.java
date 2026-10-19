package com.thelastplayer.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.thelastplayer.EchoConfig;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;

/** Simple in-game Forge config screen for selecting Gemini/Groq and entering the API key. */
public final class EchoConfigScreen extends Screen {
    private final Screen parent;
    private EchoConfig.AIProvider provider;
    private TextFieldWidget apiKeyField;
    private TextFieldWidget modelField;
    private Button providerButton;
    private Button enabledButton;
    private boolean aiEnabled;
    private String status = "API keys are saved locally in the Forge config file.";

    public EchoConfigScreen(Screen parent) {
        super(new StringTextComponent("THE LAST PLAYER - ECHO AI"));
        this.parent = parent;
        EchoConfig.AIProvider current = EchoConfig.AI_PROVIDER.get();
        this.provider = current == EchoConfig.AIProvider.GROQ ? EchoConfig.AIProvider.GROQ : EchoConfig.AIProvider.GEMINI;
        this.aiEnabled = EchoConfig.AI_ENABLED.get();
    }

    @Override
    protected void init() {
        int center = this.width / 2;
        int fieldWidth = Math.min(320, this.width - 40);
        int left = center - fieldWidth / 2;

        this.providerButton = this.addButton(new Button(left, 48, fieldWidth, 20,
                new StringTextComponent(providerLabel()), button -> switchProvider()));
        this.enabledButton = this.addButton(new Button(left, 73, fieldWidth, 20,
                new StringTextComponent(enabledLabel()), button -> {
                    aiEnabled = !aiEnabled;
                    button.setMessage(new StringTextComponent(enabledLabel()));
                }));

        this.apiKeyField = new TextFieldWidget(this.font, left, 118, fieldWidth, 20, new StringTextComponent("API Key"));
        this.apiKeyField.setMaxLength(512);
        this.apiKeyField.setSuggestion("Paste your Gemini or Groq API key here");
        this.addButton(this.apiKeyField);

        this.modelField = new TextFieldWidget(this.font, left, 164, fieldWidth, 20, new StringTextComponent("Model"));
        this.modelField.setMaxLength(128);
        this.modelField.setSuggestion(defaultModel());
        this.addButton(this.modelField);
        loadProviderFields();

        int buttonWidth = Math.min(150, (fieldWidth - 10) / 2);
        int gap = 10;
        int rowWidth = buttonWidth * 2 + gap;
        int buttonLeft = center - rowWidth / 2;
        this.addButton(new Button(buttonLeft, this.height - 36, buttonWidth, 20,
                new StringTextComponent("Save"), button -> saveAndClose()));
        this.addButton(new Button(buttonLeft + buttonWidth + gap, this.height - 36, buttonWidth, 20,
                new StringTextComponent("Cancel"), button -> this.minecraft.setScreen(this.parent)));
        this.setInitialFocus(this.apiKeyField);
    }

    private void switchProvider() {
        provider = provider == EchoConfig.AIProvider.GEMINI ? EchoConfig.AIProvider.GROQ : EchoConfig.AIProvider.GEMINI;
        providerButton.setMessage(new StringTextComponent(providerLabel()));
        loadProviderFields();
        status = "Provider changed. Save to apply.";
    }

    private void loadProviderFields() {
        if (apiKeyField == null || modelField == null) return;
        if (provider == EchoConfig.AIProvider.GROQ) {
            apiKeyField.setValue(EchoConfig.GROQ_API_KEY.get());
            modelField.setValue(nonBlank(EchoConfig.GROQ_MODEL.get(), "llama-3.3-70b-versatile"));
            apiKeyField.setSuggestion("Groq API key");
        } else {
            apiKeyField.setValue(EchoConfig.GEMINI_API_KEY.get());
            modelField.setValue(nonBlank(EchoConfig.GEMINI_MODEL.get(), "gemini-2.5-flash"));
            apiKeyField.setSuggestion("Google AI Studio API key");
        }
    }

    private void saveAndClose() {
        String key = apiKeyField.getValue().trim();
        String model = modelField.getValue().trim();
        if (model.isEmpty()) model = defaultModel();

        EchoConfig.AI_PROVIDER.set(provider);
        EchoConfig.AI_ENABLED.set(aiEnabled);
        if (provider == EchoConfig.AIProvider.GROQ) {
            EchoConfig.GROQ_API_KEY.set(key);
            EchoConfig.GROQ_MODEL.set(model);
        } else {
            EchoConfig.GEMINI_API_KEY.set(key);
            EchoConfig.GEMINI_MODEL.set(model);
        }

        try {
            EchoConfig.SPEC.save();
            this.minecraft.setScreen(this.parent);
        } catch (RuntimeException ex) {
            status = "Could not save config. Edit config/thelastplayer-common.toml manually.";
        }
    }

    private String providerLabel() {
        return "Provider: " + provider.name() + " (click to switch)";
    }

    private String enabledLabel() {
        return "AI side replies: " + (aiEnabled ? "ON" : "OFF");
    }

    private String defaultModel() {
        return provider == EchoConfig.AIProvider.GROQ ? "llama-3.3-70b-versatile" : "gemini-2.5-flash";
    }

    private static String nonBlank(String value, String fallback) {
        return value == null || value.trim().isEmpty() ? fallback : value.trim();
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        drawCenteredString(matrixStack, this.font, this.title, this.width / 2, 20, 0xFFFFFF);
        int center = this.width / 2;
        this.font.draw(matrixStack, "Choose a provider", center - Math.min(160, (this.width - 40) / 2), 37, 0xC8DFFF);
        this.font.draw(matrixStack, "API Key", center - Math.min(160, (this.width - 40) / 2), 105, 0xFFFFFF);
        this.font.draw(matrixStack, "Model name", center - Math.min(160, (this.width - 40) / 2), 151, 0xFFFFFF);
        drawCenteredString(matrixStack, this.font, "Keep your API key private; it is stored in the local config file.",
                center, 195, 0xAAAAAA);
        drawCenteredString(matrixStack, this.font, status, center, 211, 0xE6D38A);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public void tick() {
        super.tick();
        if (apiKeyField != null) apiKeyField.tick();
        if (modelField != null) modelField.tick();
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }
}
