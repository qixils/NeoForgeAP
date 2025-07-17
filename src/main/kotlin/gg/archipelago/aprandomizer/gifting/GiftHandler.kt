package gg.archipelago.aprandomizer.gifting

import dev.koifysh.archipelago.parts.NetworkPlayer
import gg.archipelago.aprandomizer.APRandomizer
import gg.archipelago.aprandomizer.APRegistries
import gg.archipelago.aprandomizer.ap.APClient
import kotlinx.coroutines.*
import kotlinx.coroutines.future.asCompletableFuture
import net.leloubil.archipelago.gifting.api.CanGiftResult
import net.leloubil.archipelago.gifting.api.GiftItem
import net.leloubil.archipelago.gifting.api.GiftingService
import net.leloubil.archipelago.gifting.api.SendGiftResult
import net.minecraft.core.RegistryAccess
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeManager
import java.util.concurrent.CompletableFuture
import kotlin.jvm.optionals.getOrNull

private val CanGiftResult.CanGiftError.playerFacingMessage
    get() = when (this) {
        CanGiftResult.CanGiftError.DataStorageWriteError -> "Failed to write to data storage."
        is CanGiftResult.CanGiftError.DataVersionTooLow -> "The recipient's data version is too low to receive gifts. Minimum required: ${this.recipientMinimumVersion}."

        CanGiftResult.CanGiftError.GiftBoxClosed -> "The recipient's gift box is closed."
        is CanGiftResult.CanGiftError.NoMatchingTraits -> "The recipient does not accept any of the traits of this gift. Accepted traits: ${
            this.recipientAcceptedTraits.joinToString(
                ", "
            )
        }."

        is CanGiftResult.CanGiftError.NoGiftBox -> "The player doesn't accept gifts."
    }

@OptIn(DelicateCoroutinesApi::class)
class GiftHandler(client: APClient,val matcher: MinecraftGiftMatcher) {
    fun openGiftBox(): CompletableFuture<Boolean> {
        return GlobalScope.async { giftingService.openGiftBox(true, emptyList()) }.asCompletableFuture()
    }

    private val giftingService = GiftingService(client)

    var recepTask: Job? = null

    fun startReception(registryAccess: RegistryAccess, recipeManager: RecipeManager) {
        if (recepTask != null) return
        recepTask = GlobalScope.launch {
            println("Starting gift reception")
            giftingService.receivedGifts.collect { gift ->
                println("Received gift: $gift")
                val removed = giftingService.removeGiftsFromBox(gift)
                if (!(removed?.contains(gift.id) ?: false)){
                    println("Failed to remove gift from box: $gift")
                    return@collect
                }
                val recipient = APRandomizer.server!!.playerList.players.random() //todo
                println("Received gift $gift")
                if (gift.isRefund) {
                    val item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(gift.item.name)).getOrNull()
                    if (item == null) {
                        println("Dropping refund : $gift")
                    } else {
                        //todo handle complex refunds (like potions) by saving sent gift ids and itemstacks maybe ?
                        recipient.addItem(ItemStack(item, gift.amount))
                    }
                }
                else {
                    val itemStack: ItemStack? = context(registryAccess.lookupOrThrow(APRegistries.GIFT_TRAITS)) {
                        matcher.resolveGift(gift.item.traits, recipeManager)?.copyWithCount(gift.amount)
                    }
                    if (itemStack == null) {
                        println("Failed to resolve item for gift: $gift")
                        giftingService.refundGift(gift)
                        return@collect
                    }
                    val displayStack = itemStack.copy()
                    val res = recipient.addItem(itemStack)
                    if (!res) {
                        println("Failed to add gift item to player ${recipient.name}. Item: $displayStack, Amount: ${gift.amount}")
                        giftingService.refundGift(gift)
                    } else {
                        println("Gift item added to player ${recipient.name}. Item: $displayStack, Amount: ${gift.amount}")
                    }
                }

            }
        }
    }

    fun stopReception() {
        recepTask?.cancel()
    }

    fun closeGiftBox(): CompletableFuture<Boolean> {
        return GlobalScope.async {
            giftingService.closeGiftBox();
        }.asCompletableFuture()
    }

    /**
     * Checks if the item can be sent to the recipient.
     *
     * @param stack The item
     * @param recipient The player receiving the item.
     * @return A user-facing string indicating the reason if it cannot be sent, or null if it can be sent.
     */
    context(registryAccess: RegistryAccess)
    fun canSendItem(stack: ItemStack, recipient: NetworkPlayer, recipeManager: RecipeManager): CompletableFuture<String?> {
        return GlobalScope.async {
            val res = giftingService.canGiftToPlayer(
                recipient.slot, recipient.team, getGiftItem(stack, recipeManager).traits.map { it.name })


            when (res) {
                is CanGiftResult.CanGiftError -> res.playerFacingMessage
                is CanGiftResult.CanGiftSuccess -> null
            }
        }.asCompletableFuture()
    }

    /**
     * Gifts the item to the recipient.
     *
     * @param stack The item to be gifted.
     * @param recipient The player receiving the item.
     * @return A user-facing string indicating the result of the gifting operation, or null if successful.
     */
    context(registryAccess: RegistryAccess)
    fun giftItem(stack: ItemStack, recipient: NetworkPlayer, recipeManager: RecipeManager): CompletableFuture<String?> {
        return GlobalScope.async {
            val item = getGiftItem(stack, recipeManager)
            println("Gifting item: $item to recipient: ${recipient.name} (slot: ${recipient.slot}, team: ${recipient.team})")
            val res = giftingService.sendGift(
                item = item,
                amount = stack.count,
                recipientPlayerSlot = recipient.slot,
                recipientPlayerTeam = recipient.team
            )

            when (res) {
                SendGiftResult.SendGiftFailure.DataStorageWriteError -> "Failed to write to data storage."
                is SendGiftResult.SendGiftFailure.CannotGift -> "Cannot gift: ${res.reason.playerFacingMessage}"
                SendGiftResult.SendGiftSuccess -> null
            }
        }.asCompletableFuture()
    }

    context(registryAccess: RegistryAccess)
    fun getGiftItem(itemStack: ItemStack, recipeManager: RecipeManager): GiftItem {
        val item = itemStack.item
        val orThrow = registryAccess.lookupOrThrow(APRegistries.GIFT_TRAITS)
        val traits = context(orThrow) {
            matcher.getItemStackTraits(itemStack, recipeManager)
        }
        return GiftItem(
            name = BuiltInRegistries.ITEM.getKey(item).toString(),
            traits = traits
        )
    }


}



