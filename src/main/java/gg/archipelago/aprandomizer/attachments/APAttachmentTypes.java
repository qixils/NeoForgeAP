package gg.archipelago.aprandomizer.attachments;

import gg.archipelago.aprandomizer.APRandomizer;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class APAttachmentTypes {
    public static final DeferredRegister<AttachmentType<?>> REGISTER = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, APRandomizer.MODID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<APPlayerAttachment>> AP_PLAYER = REGISTER.register("ap_player", () -> AttachmentType
            .builder(APPlayerAttachment::new)
            .serialize(APPlayerAttachment.CODEC)
            .copyOnDeath()
            .build());
}
