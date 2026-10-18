package com.raf.culinaryloop.data.datasource

import com.raf.culinaryloop.data.model.MenuCategoryDto
import com.raf.culinaryloop.data.model.MenuItemDto
import com.raf.culinaryloop.data.model.ModifierGroupDto
import com.raf.culinaryloop.data.model.ModifierOptionDto
import com.raf.culinaryloop.data.model.StoreDto

/**
 * High-fidelity offline and sandbox dataset representing ESB Loop / ESB Order merchants.
 */
object MockEsbData {

    val stores: List<StoreDto> = listOf(
        StoreDto(
            branchId = "esb_branch_001",
            branchName = "CulinaryLoop Senopati Flagship",
            brandName = "CulinaryLoop Artisan Kitchen",
            address = "Jl. Senopati No. 42, Kebayoran Baru",
            city = "Jakarta Selatan",
            latitude = -6.2305,
            longitude = 106.8112,
            operatingHours = "08:00 - 23:00",
            isOpen = true,
            supportedChannels = listOf("dine_in", "takeaway", "delivery"),
            phoneNumber = "+62 21 5790 1234",
            bannerImageUrl = "https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=800&auto=format&fit=crop&q=80"
        ),
        StoreDto(
            branchId = "esb_branch_002",
            branchName = "CulinaryLoop SCBD Gourmet Hub",
            brandName = "CulinaryLoop Artisan Kitchen",
            address = "Pacific Century Place, Ground Floor, Lot 10 SCBD",
            city = "Jakarta Selatan",
            latitude = -6.2255,
            longitude = 106.8091,
            operatingHours = "07:00 - 22:00",
            isOpen = true,
            supportedChannels = listOf("dine_in", "takeaway"),
            phoneNumber = "+62 21 5150 4321",
            bannerImageUrl = "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=800&auto=format&fit=crop&q=80"
        ),
        StoreDto(
            branchId = "esb_branch_003",
            branchName = "CulinaryLoop Grand Indonesia",
            brandName = "CulinaryLoop Artisan Kitchen",
            address = "West Mall Level 3A, Jl. M.H. Thamrin No. 1",
            city = "Jakarta Pusat",
            latitude = -6.1952,
            longitude = 106.8208,
            operatingHours = "10:00 - 22:00",
            isOpen = true,
            supportedChannels = listOf("dine_in", "takeaway", "delivery"),
            phoneNumber = "+62 21 2358 8899",
            bannerImageUrl = "https://images.unsplash.com/photo-1552566626-52f8b828add9?w=800&auto=format&fit=crop&q=80"
        ),
        StoreDto(
            branchId = "esb_branch_004",
            branchName = "CulinaryLoop PIK Avenue",
            brandName = "CulinaryLoop Artisan Kitchen",
            address = "PIK Avenue Mall Ground Floor, Pantai Indah Kapuk",
            city = "Jakarta Utara",
            latitude = -6.1104,
            longitude = 106.7388,
            operatingHours = "09:00 - 22:00",
            isOpen = true,
            supportedChannels = listOf("dine_in", "takeaway", "delivery"),
            phoneNumber = "+62 21 2928 7700",
            bannerImageUrl = "https://images.unsplash.com/photo-1544025162-d76694265947?w=800&auto=format&fit=crop&q=80"
        ),
        StoreDto(
            branchId = "esb_branch_005",
            branchName = "CulinaryLoop Dharmawangsa Artisanal",
            brandName = "CulinaryLoop Artisan Kitchen",
            address = "Jl. Dharmawangsa Raya No. 18, Kebayoran Baru",
            city = "Jakarta Selatan",
            latitude = -6.2520,
            longitude = 106.7995,
            operatingHours = "08:30 - 22:30",
            isOpen = false,
            supportedChannels = listOf("dine_in", "takeaway"),
            phoneNumber = "+62 21 7278 9911",
            bannerImageUrl = "https://images.unsplash.com/photo-1554118811-1e0d58224f24?w=800&auto=format&fit=crop&q=80"
        )
    )

    val categories: List<MenuCategoryDto> = listOf(
        MenuCategoryDto("cat_coffee", "Coffee & Specialty Drinks", 1),
        MenuCategoryDto("cat_mains", "Main Culinary Courses", 2),
        MenuCategoryDto("cat_snacks", "Artisanal Snacks & Sides", 3),
        MenuCategoryDto("cat_desserts", "Sweet Delights & Gelato", 4)
    )

    private val sugarModifierGroup = ModifierGroupDto(
        groupId = "mod_sugar",
        title = "Sugar Sweetness",
        isRequired = true,
        minSelections = 1,
        maxSelections = 1,
        options = listOf(
            ModifierOptionDto("opt_sugar_100", "Normal Sweet (100%)", 0L),
            ModifierOptionDto("opt_sugar_70", "Less Sweet (70%)", 0L),
            ModifierOptionDto("opt_sugar_30", "Slightly Sweet (30%)", 0L),
            ModifierOptionDto("opt_sugar_0", "Unsweetened (0%)", 0L)
        )
    )

    private val iceModifierGroup = ModifierGroupDto(
        groupId = "mod_ice",
        title = "Ice Preference",
        isRequired = true,
        minSelections = 1,
        maxSelections = 1,
        options = listOf(
            ModifierOptionDto("opt_ice_normal", "Normal Ice", 0L),
            ModifierOptionDto("opt_ice_less", "Less Ice", 0L),
            ModifierOptionDto("opt_ice_none", "No Ice", 0L)
        )
    )

    private val milkModifierGroup = ModifierGroupDto(
        groupId = "mod_milk",
        title = "Milk Choice",
        isRequired = false,
        minSelections = 0,
        maxSelections = 1,
        options = listOf(
            ModifierOptionDto("opt_milk_fresh", "Fresh Dairy Milk", 0L),
            ModifierOptionDto("opt_milk_oat", "Oatside Barista Oat Milk", 8000L),
            ModifierOptionDto("opt_milk_almond", "Roasted Almond Milk", 10000L)
        )
    )

    private val drinkToppingsGroup = ModifierGroupDto(
        groupId = "mod_toppings",
        title = "Gourmet Toppings",
        isRequired = false,
        minSelections = 0,
        maxSelections = 3,
        options = listOf(
            ModifierOptionDto("opt_top_boba", "Brown Sugar Boba", 6000L),
            ModifierOptionDto("opt_top_jelly", "Espresso Jelly", 5000L),
            ModifierOptionDto("opt_top_cheese", "Hokkaido Cheese Foam", 7000L)
        )
    )

    private val eggStyleGroup = ModifierGroupDto(
        groupId = "mod_egg",
        title = "Egg Cooking Style",
        isRequired = false,
        minSelections = 0,
        maxSelections = 1,
        options = listOf(
            ModifierOptionDto("opt_egg_sunny", "Sunny Side Up (Half Cooked)", 0L),
            ModifierOptionDto("opt_egg_well", "Sunny Side Up (Well Done)", 0L),
            ModifierOptionDto("opt_egg_scrambled", "Creamy Scrambled Egg", 4000L)
        )
    )

    private val spicyLevelGroup = ModifierGroupDto(
        groupId = "mod_spice",
        title = "Spiciness Level",
        isRequired = true,
        minSelections = 1,
        maxSelections = 1,
        options = listOf(
            ModifierOptionDto("opt_spice_mild", "Mild / Not Spicy", 0L),
            ModifierOptionDto("opt_spice_med", "Medium Spicy (Level 1)", 0L),
            ModifierOptionDto("opt_spice_hot", "Fire Sambal (Level 3)", 0L)
        )
    )

    val menuItems: List<MenuItemDto> = listOf(
        // Drinks
        MenuItemDto(
            itemId = "item_c01",
            categoryId = "cat_coffee",
            name = "Gula Aren Espresso Macchiato",
            description = "Double ristretto poured over organic volcanic palm sugar syrup and chilled milk.",
            basePrice = 38000L,
            imageUrl = "https://images.unsplash.com/photo-1541167760496-1628856ab772?w=800&auto=format&fit=crop&q=80",
            isAvailable = true,
            modifierGroups = listOf(sugarModifierGroup, iceModifierGroup, milkModifierGroup, drinkToppingsGroup)
        ),
        MenuItemDto(
            itemId = "item_c02",
            categoryId = "cat_coffee",
            name = "Pandan Cream Cold Brew",
            description = "16-hour steeped Sumatra beans topped with aromatic fresh pandan infused sea-salt cream.",
            basePrice = 42000L,
            imageUrl = "https://images.unsplash.com/photo-1517701604599-bb29b565090c?w=800&auto=format&fit=crop&q=80",
            isAvailable = true,
            modifierGroups = listOf(sugarModifierGroup, iceModifierGroup, drinkToppingsGroup)
        ),
        MenuItemDto(
            itemId = "item_c03",
            categoryId = "cat_coffee",
            name = "Kyoto Ceremonial Matcha Latte",
            description = "First-harvest Uji ceremonial matcha whisked with artisanal steamed milk.",
            basePrice = 45000L,
            imageUrl = "https://images.unsplash.com/photo-1536256263959-770b48d82b0a?w=800&auto=format&fit=crop&q=80",
            isAvailable = true,
            modifierGroups = listOf(sugarModifierGroup, iceModifierGroup, milkModifierGroup)
        ),

        // Mains
        MenuItemDto(
            itemId = "item_m01",
            categoryId = "cat_mains",
            name = "Wagyu Beef Truffle Fried Rice",
            description = "Fragrant wok-fried Jasmine rice with MB6+ Wagyu beef cubes, white truffle essence, and garlic chips.",
            basePrice = 85000L,
            imageUrl = "https://images.unsplash.com/photo-1603133872878-684f208fb84b?w=800&auto=format&fit=crop&q=80",
            isAvailable = true,
            modifierGroups = listOf(spicyLevelGroup, eggStyleGroup)
        ),
        MenuItemDto(
            itemId = "item_m02",
            categoryId = "cat_mains",
            name = "Crispy Duck Sambal Matah Bowl",
            description = "Spiced crispy Balinese duck thigh served with shallot lemongrass sambal and warm lime rice.",
            basePrice = 78000L,
            imageUrl = "https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?w=800&auto=format&fit=crop&q=80",
            isAvailable = true,
            modifierGroups = listOf(spicyLevelGroup, eggStyleGroup)
        ),
        MenuItemDto(
            itemId = "item_m03",
            categoryId = "cat_mains",
            name = "Salmon Teriyaki Poke Don",
            description = "Norwegian salmon grilled with artisanal house teriyaki sauce over Japanese short-grain rice.",
            basePrice = 88000L,
            imageUrl = "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=800&auto=format&fit=crop&q=80",
            isAvailable = true,
            modifierGroups = emptyList()
        ),

        // Snacks
        MenuItemDto(
            itemId = "item_s01",
            categoryId = "cat_snacks",
            name = "Truffle Parmesan Hand-Cut Fries",
            description = "Crispy russet potato fries tossed in aromatic truffle oil, fresh herbs, and Grana Padano.",
            basePrice = 45000L,
            imageUrl = "https://images.unsplash.com/photo-1573080496219-bb080dd4f877?w=800&auto=format&fit=crop&q=80",
            isAvailable = true,
            modifierGroups = emptyList()
        ),
        MenuItemDto(
            itemId = "item_s02",
            categoryId = "cat_snacks",
            name = "Crispy Salted Egg Chicken Skin",
            description = "Ultra-crispy hand-battered chicken skin glazed in rich curry leaf salted egg yolk reduction.",
            basePrice = 39000L,
            imageUrl = "https://images.unsplash.com/photo-1626082927389-6cd097cdc6ec?w=800&auto=format&fit=crop&q=80",
            isAvailable = true,
            modifierGroups = emptyList()
        ),

        // Desserts
        MenuItemDto(
            itemId = "item_d01",
            categoryId = "cat_desserts",
            name = "Burnt Basque Cheesecake",
            description = "Silky caramelized Spanish cheesecake with a molten mascarpone center.",
            basePrice = 48000L,
            imageUrl = "https://images.unsplash.com/photo-1533134242443-d4fd215305ad?w=800&auto=format&fit=crop&q=80",
            isAvailable = true,
            modifierGroups = emptyList()
        ),
        MenuItemDto(
            itemId = "item_d02",
            categoryId = "cat_desserts",
            name = "Es Cendol Durian Gelato",
            description = "House-churned pandan jelly ribbons, coconut milk foam, and authentic Monthong durian puree.",
            basePrice = 52000L,
            imageUrl = "https://images.unsplash.com/photo-1501443762994-82bd5dace89a?w=800&auto=format&fit=crop&q=80",
            isAvailable = true,
            modifierGroups = emptyList()
        )
    )
}
