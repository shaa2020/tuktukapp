package com.example.data.remote

import com.example.data.model.*

object MockTukTukInventory {

    val guides = listOf(
        GuideInfo(
            id = "guide_diogo",
            name = "Diogo Silva",
            avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=400&q=80",
            rating = 4.98,
            reviewCount = 312,
            languages = listOf("English", "Portuguese", "Spanish"),
            bio = "Lisbon native with 7 years of private tuk-tuk guiding experience. Certified storyteller & foodie enthusiast.",
            phone = "+351 912 345 678",
            whatsapp = "+351912345678"
        ),
        GuideInfo(
            id = "guide_inês",
            name = "Inês Santos",
            avatarUrl = "https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&w=400&q=80",
            rating = 4.95,
            reviewCount = 248,
            languages = listOf("English", "French", "Portuguese"),
            bio = "History graduate passionate about Sintra's romantic architecture and Lisbon's hidden miradouros.",
            phone = "+351 923 456 789",
            whatsapp = "+351923456789"
        ),
        GuideInfo(
            id = "guide_miguel",
            name = "Miguel Ferreira",
            avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=400&q=80",
            rating = 4.97,
            reviewCount = 189,
            languages = listOf("English", "Portuguese", "Italian"),
            bio = "Coastal specialist for Cascais, Cabo da Roca and Nazaré big wave viewpoints.",
            phone = "+351 934 567 890",
            whatsapp = "+351934567890"
        )
    )

    val commonExtras = listOf(
        TourExtra("ext_pastel", "Pastéis de Belém & Port Wine Basket", "Warm authentic pastel de nata box paired with aged Tawny Port wine for your party", 18.0, "BakeryDining"),
        TourExtra("ext_champagne", "Portuguese Vinho Verde & Grapes", "Chilled bottle of local Vinho Verde white wine served at a panoramic scenic stop", 25.0, "LocalBar"),
        TourExtra("ext_photo", "Professional HD Vacation Photo Package", "Your guide captures 25+ high-res candid photos at top miradouros", 35.0, "CameraAlt"),
        TourExtra("ext_vip_pickup", "Doorstep Hotel / Cruise Terminal VIP Pickup", "Direct private pickup from your lobby or cruise terminal gate", 20.0, "Hotel"),
        TourExtra("ext_wedding_pack", "Celebration & Romantic Flower Garland", "Vehicle festively decorated with fresh local floral garlands and celebration toast", 40.0, "Celebration"),
        TourExtra("ext_audio", "Premium Multi-language Audio Headset", "Crisp audio narration synced with real-time GPS locations", 10.0, "Headphones"),
        TourExtra("ext_extend", "Extended 30-Min Sunset Stop", "Extra time at Miradouro da Senhora do Monte with complimentary Pastéis de Nátas", 30.0, "AccessTime")
    )

    val commonPickupPoints = listOf(
        PickupPoint("pk_hotel", "Hotel Pickup (Lisbon Central)", "Direct pickup from any hotel/Airbnb in Lisbon city center", isHotelPickup = true, extraFeeEur = 0.0),
        PickupPoint("pk_rossio", "Rossio Square Meeting Point", "Praça do Rossio 12, 1100-200 Lisboa", isHotelPickup = false, extraFeeEur = 0.0),
        PickupPoint("pk_comercio", "Praça do Comércio Arch", "Praça do Comércio, 1100-148 Lisboa", isHotelPickup = false, extraFeeEur = 0.0),
        PickupPoint("pk_airport", "Lisbon Airport (LIS) Arrival Gate", "Aeroporto de Lisboa, Alameda das Comunidades Portuguesas", isHotelPickup = true, extraFeeEur = 25.0)
    )

    val tours = listOf(
        Tour(
            id = "tour_lisbon_7_hills",
            title = "Lisbon 7 Hills Private Electric Tuk-Tuk Experience",
            tagline = "Zip through Alfama, Graça, Chiado & historic viewpoints in eco-comfort",
            destination = "Lisbon",
            category = TourCategory.HISTORIC,
            description = "Explore the timeless charm of Lisbon's historic quarters on our 100% quiet electric tuk-tuks. Climb the famous steep hills effortlessly and access narrow medieval alleys where buses cannot enter. Stop at breathtaking scenic lookouts (Miradouros) like Senhora do Monte, visit Lisbon Cathedral (Sé), and immerse yourself in the birthplaces of Fado.",
            mainImageUrl = "https://images.unsplash.com/photo-1588614959060-4d144f28b207?auto=format&fit=crop&w=1000&q=80",
            galleryImages = listOf(
                "https://images.unsplash.com/photo-1588614959060-4d144f28b207?auto=format&fit=crop&w=1000&q=80",
                "https://images.unsplash.com/photo-1509824227185-9c5a01ceba0d?auto=format&fit=crop&w=1000&q=80",
                "https://images.unsplash.com/photo-1548705085-101177834f47?auto=format&fit=crop&w=1000&q=80"
            ),
            rating = 4.96,
            reviewCount = 428,
            durationHours = 2.0,
            basePriceEur = 85.0,
            perGuestPriceEur = 15.0,
            experienceType = ExperienceType.PRIVATE,
            languages = listOf("English", "Portuguese", "Spanish", "French"),
            pickupAvailable = true,
            freeCancellationHours = 24,
            instantConfirmation = true,
            isFeatured = true,
            isPopular = true,
            isRecommended = true,
            isLastMinute = false,
            highlights = listOf(
                TourHighlight("Miradouro da Senhora do Monte", "Highest panoramic viewpoint over the red roofs, Tagus river and 25 de Abril Bridge."),
                TourHighlight("Alfama Medieval Quarter", "Wander through centuries-old alleys, traditional Fado taverns, and whitewashed houses."),
                TourHighlight("Sé de Lisboa (Lisbon Cathedral)", "Romanesque landmark built in 1147 with imposing gothic towers."),
                TourHighlight("Pastel de Nata Tasting", "Sample warm, freshly baked custard tarts from an authentic local bakery.")
            ),
            itinerary = listOf(
                ItineraryStep(1, "Hotel Pickup & Safety Briefing", 15, "Meet your private guide and board your eco-friendly electric tuk-tuk.", "Lisbon Hotel / Meeting Point"),
                ItineraryStep(2, "Climb to Graça & Senhora do Monte", 35, "Ascend the iconic steep hills and take postcard photos at the city's highest outlook.", "Miradouro Senhora do Monte"),
                ItineraryStep(3, "Alfama & Lisbon Cathedral", 45, "Navigate winding cobblestone streets, stopping at Sé Cathedral and Santa Luzia viewpoint.", "Alfama Quarter"),
                ItineraryStep(4, "Chiado & Baixa Return", 25, "Drive past Carmo Convent, Santa Justa Lift, and return comfortably.", "Praça do Comércio")
            ),
            included = listOf("Private electric tuk-tuk & professional guide", "Hotel pickup & drop-off (Lisbon central)", "Live commentary in your selected language", "Warm Pastel de Nata & bottle of water", "Complimentary blankets in cool weather"),
            excluded = listOf("Monuments entry tickets", "Personal expenses", "Gratuities"),
            availableTimeSlots = listOf("09:00 AM", "11:30 AM", "02:00 PM", "04:30 PM", "06:30 PM"),
            meetingPointAddress = "Praça do Comércio 12, 1100-148 Lisboa",
            pickupPoints = commonPickupPoints,
            availableExtras = commonExtras,
            assignedGuide = guides[0],
            reviews = listOf(
                TourReview("rev_1", "Sarah Jenkins", "United States", "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=200&q=80", 5, "2026-08-02", "Unforgettable way to see Lisbon!", "Diogo was incredible! He navigated the tightest Alfama streets with ease and shared amazing historical context. The views from Senhora do Monte left us speechless!"),
                TourReview("rev_2", "Marco Rossi", "Italy", "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=200&q=80", 5, "2026-07-28", "Super smooth and private", "We brought our parents and the electric tuk-tuk made climbing Lisbon's steep hills so relaxing. High quality brand and flawless service.")
            )
        ),
        Tour(
            id = "tour_sintra_royal_palaces",
            title = "Fairytale Sintra Royal Palaces Private Tour",
            tagline = "Discover Pena Palace, Quinta da Regaleira & mystical Serra de Sintra",
            destination = "Sintra",
            category = TourCategory.DAY_TRIP,
            description = "Journey to Sintra, UNESCO World Heritage site and fairytale mountain retreat of Portuguese royalty. Ride past lush mountain pine forests, romantic mist-shrouded palaces, hidden grottoes, and exotic gardens. Skip steep uphill walking between monuments with your dedicated electric tuk-tuk guide.",
            mainImageUrl = "https://images.unsplash.com/photo-1590077428593-a55bb07c4665?auto=format&fit=crop&w=1000&q=80",
            galleryImages = listOf(
                "https://images.unsplash.com/photo-1590077428593-a55bb07c4665?auto=format&fit=crop&w=1000&q=80",
                "https://images.unsplash.com/photo-1584646098378-0874589d76b1?auto=format&fit=crop&w=1000&q=80"
            ),
            rating = 4.98,
            reviewCount = 310,
            durationHours = 4.0,
            basePriceEur = 140.0,
            perGuestPriceEur = 20.0,
            experienceType = ExperienceType.PRIVATE,
            languages = listOf("English", "Portuguese", "Spanish", "French"),
            pickupAvailable = true,
            freeCancellationHours = 24,
            instantConfirmation = true,
            isFeatured = true,
            isPopular = true,
            isRecommended = true,
            isLastMinute = true,
            highlights = listOf(
                TourHighlight("Pena National Palace", "The iconic colorful Romanticist palace perched high on Sintra peak."),
                TourHighlight("Quinta da Regaleira", "Mystical estate with the famous Initiation Well subterranean spiral staircases."),
                TourHighlight("Moorish Castle Viewpoints", "8th-century stone fortification walls snake along mountain ridges."),
                TourHighlight("Travesseiros de Sintra", "Taste Sintra's legendary almond puff pastry at Casa Piriquita.")
            ),
            itinerary = listOf(
                ItineraryStep(1, "Sintra Station / Hotel Meeting", 15, "Meet guide in Sintra village and begin mountain ascent.", "Sintra Village"),
                ItineraryStep(2, "Ascend to Pena Palace Gates", 45, "Avoid traffic and line queues with nimble tuk-tuk mountain access.", "Pena Palace"),
                ItineraryStep(3, "Quinta da Regaleira Gardens", 60, "Explore esoteric initiation wells, subterranean tunnels, and waterfalls.", "Quinta da Regaleira"),
                ItineraryStep(4, "Historic Center & Pastry Stop", 60, "Sample Travesseiros pastry and enjoy free time in Sintra center.", "Sintra Old Town")
            ),
            included = listOf("Private electric tuk-tuk transport in Sintra", "Dedicated mountain guide", "Pastry tasting ticket", "Bottled water"),
            excluded = listOf("Pena Palace & Regaleira admission tickets (can be pre-purchased)", "Lunch"),
            availableTimeSlots = listOf("09:30 AM", "01:30 PM", "03:30 PM"),
            meetingPointAddress = "Sintra Train Station, Av. Dr. Miguel Bombarda, 2710-590 Sintra",
            pickupPoints = commonPickupPoints,
            availableExtras = commonExtras,
            assignedGuide = guides[1],
            reviews = listOf(
                TourReview("rev_sintra1", "Claire Dupont", "France", "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=200&q=80", 5, "2026-08-05", "Absolute highlight of our Portugal trip!", "Inês was remarkable. Without the tuk-tuk we would have been exhausted walking up those steep Sintra hills. Unforgettable day!")
            )
        ),
        Tour(
            id = "tour_cascais_cabo_roca",
            title = "Cascais Coastal Breeze & Cabo da Roca Sunset",
            tagline = "Stand at the westernmost point of continental Europe & Atlantic cliffs",
            destination = "Cascais",
            category = TourCategory.COASTAL,
            description = "Feel the crisp Atlantic ocean air as you journey along the Portuguese Riviera from the elegant seaside town of Cascais to the dramatic cliffs of Boca do Inferno (Hell's Mouth) and Cabo da Roca — the westernmost point of mainland Europe.",
            mainImageUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=1000&q=80",
            galleryImages = listOf(
                "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=1000&q=80",
                "https://images.unsplash.com/photo-1512100356356-de1b84283e18?auto=format&fit=crop&w=1000&q=80"
            ),
            rating = 4.94,
            reviewCount = 195,
            durationHours = 3.0,
            basePriceEur = 110.0,
            perGuestPriceEur = 18.0,
            experienceType = ExperienceType.PRIVATE,
            languages = listOf("English", "Portuguese", "Spanish"),
            pickupAvailable = true,
            freeCancellationHours = 24,
            instantConfirmation = true,
            isFeatured = true,
            isPopular = false,
            isRecommended = true,
            isLastMinute = false,
            highlights = listOf(
                TourHighlight("Cabo da Roca", "Monument mark where the land ends and the sea begins (Where the land ends and the sea begins - Camões)."),
                TourHighlight("Boca do Inferno", "Natural chasm where massive waves crash against dark limestone sea arches."),
                TourHighlight("Cascais Village & Bay", "Charming fishing harbour, royal summer residences and golden beaches."),
                TourHighlight("Guincho Beach Coastline", "Surfer haven with dramatic sand dunes and rugged coastal roads.")
            ),
            itinerary = listOf(
                ItineraryStep(1, "Cascais Marina Departure", 15, "Meet guide at Cascais Marina.", "Cascais Marina"),
                ItineraryStep(2, "Boca do Inferno & Guincho Coast", 45, "Coastal drive past dramatic wave chasms and dune beaches.", "Boca do Inferno"),
                ItineraryStep(3, "Cabo da Roca Sunset Stop", 60, "Stand at the edge of Europe during magical golden hour.", "Cabo da Roca Lighthouse"),
                ItineraryStep(4, "Return to Cascais Promenade", 60, "Enjoy a scenic drive back to Cascais center.", "Cascais Center")
            ),
            included = listOf("Private electric tuk-tuk", "Local guide", "Commemorative Cabo da Roca certificate info", "Mineral water"),
            excluded = listOf("Seafood lunch", "Gratuities"),
            availableTimeSlots = listOf("10:00 AM", "02:00 PM", "05:00 PM (Sunset Slot)"),
            meetingPointAddress = "Cascais Marina, Av. Rei Humberto II de Itália, 2750-800 Cascais",
            pickupPoints = commonPickupPoints,
            availableExtras = commonExtras,
            assignedGuide = guides[2],
            reviews = listOf(
                TourReview("rev_cascais1", "John Miller", "United Kingdom", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=200&q=80", 5, "2026-08-01", "Breathtaking sunset!", "Miguel drove us out to Cabo da Roca right as the sun touched the horizon. Simply beautiful.")
            )
        ),
        Tour(
            id = "tour_lisbon_night_lights",
            title = "Lisbon Lights & Fado Night Experience",
            tagline = "Illuminated monuments, romantic viewpoints & optional Fado dining",
            destination = "Lisbon",
            category = TourCategory.SUNSET,
            description = "Experience Lisbon after dark when the city turns into a sparkling golden jewel. Drive past illuminated landmarks like Commerce Square, Belém Tower, and Santa Justa Lift. Enjoy a cozy night drive through quiet Alfama streets with optional stop at a traditional Fado tavern.",
            mainImageUrl = "https://images.unsplash.com/photo-1513584684374-8bab748fbf90?auto=format&fit=crop&w=1000&q=80",
            galleryImages = listOf(
                "https://images.unsplash.com/photo-1513584684374-8bab748fbf90?auto=format&fit=crop&w=1000&q=80"
            ),
            rating = 4.97,
            reviewCount = 162,
            durationHours = 2.0,
            basePriceEur = 95.0,
            perGuestPriceEur = 15.0,
            experienceType = ExperienceType.PRIVATE,
            languages = listOf("English", "Portuguese", "Spanish"),
            pickupAvailable = true,
            freeCancellationHours = 24,
            instantConfirmation = true,
            isFeatured = false,
            isPopular = true,
            isRecommended = true,
            isLastMinute = true,
            highlights = listOf(
                TourHighlight("Illuminated Praça do Comércio", "Grand riverside plaza shining under golden streetlights."),
                TourHighlight("Graça Night Lookout", "Gaze across the sparkling city night sky and 25th of April Bridge."),
                TourHighlight("Alfama Evening Fado Atmosphere", "Pass intimate taverns filled with soul-stirring live Portuguese singing.")
            ),
            itinerary = listOf(
                ItineraryStep(1, "Evening Hotel Pickup", 15, "Board tuk-tuk wrapped in plush warm blankets.", "Hotel Pickup"),
                ItineraryStep(2, "Night Ride to Graça Lookout", 45, "Watch Lisbon lights glow over the Tagus river.", "Miradouro de Graça"),
                ItineraryStep(3, "Belém Tower & Discovery Monument Night Lights", 45, "Pass illuminated maritime monuments in Belém.", "Belém Quarter"),
                ItineraryStep(4, "Fado District Drop-off / Return", 15, "Drop off at top Fado restaurant or hotel.", "Alfama Quarter")
            ),
            included = listOf("Private electric tuk-tuk night ride", "Fleece blankets", "Glass of Port Wine", "Hotel pickup & drop-off"),
            excluded = listOf("Fado dinner meal", "Gratuities"),
            availableTimeSlots = listOf("08:00 PM", "09:30 PM"),
            meetingPointAddress = "Praça do Comércio, Lisbon",
            pickupPoints = commonPickupPoints,
            availableExtras = commonExtras,
            assignedGuide = guides[0],
            reviews = listOf(
                TourReview("rev_night1", "Elena Vance", "Canada", "https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&w=200&q=80", 5, "2026-07-30", "Magical night in Lisbon", "The city illuminated at night is magical! Sipping Port wine while taking in the views was unforgettable.")
            )
        ),
        Tour(
            id = "tour_belem_flavors",
            title = "Belém Maritime & Foodie Secrets Tour",
            tagline = "Discover Age of Discovery history & taste authentic Pastéis de Belém",
            destination = "Lisbon",
            category = TourCategory.FOOD_WINE,
            description = "Follow the footsteps of Portuguese explorers Vasco da Gama and Magellan along the Tagus riverbank to Belém. Marvel at Jerónimos Monastery, Belém Tower, and Padrão dos Descobrimentos. Skip the legendary long line at the original 1837 Pastéis de Belém factory for hot custard tarts!",
            mainImageUrl = "https://images.unsplash.com/photo-1548705085-101177834f47?auto=format&fit=crop&w=1000&q=80",
            galleryImages = listOf(
                "https://images.unsplash.com/photo-1548705085-101177834f47?auto=format&fit=crop&w=1000&q=80"
            ),
            rating = 4.95,
            reviewCount = 280,
            durationHours = 2.5,
            basePriceEur = 90.0,
            perGuestPriceEur = 15.0,
            experienceType = ExperienceType.PRIVATE,
            languages = listOf("English", "Portuguese", "Spanish", "German"),
            pickupAvailable = true,
            freeCancellationHours = 24,
            instantConfirmation = true,
            isFeatured = false,
            isPopular = true,
            isRecommended = true,
            isLastMinute = false,
            highlights = listOf(
                TourHighlight("Skip-the-Line Pastéis de Belém", "Taste warm cinnamon-dusted tarts directly from the secret recipe bakery."),
                TourHighlight("Mosteiro dos Jerónimos", "Magnificent Manueline gothic architecture monument."),
                TourHighlight("Torre de Belém", "16th-century fortress guarding the river entrance."),
                TourHighlight("25 de Abril Bridge & MAAT Museum", "Modern riverside architectural contrast.")
            ),
            itinerary = listOf(
                ItineraryStep(1, "Riverside Drive along Tagus", 30, "Glide westwards past Cais do Sodré and Alcantara.", "Tagus Waterfront"),
                ItineraryStep(2, "Jerónimos & Pastéis de Belém", 45, "Enjoy fast access to Pastéis de Belém custard tarts.", "Belém Bakery"),
                ItineraryStep(3, "Belém Tower & Monument to Discoveries", 45, "Photo stops at historic sea fort and monument.", "Belém Waterfront"),
                ItineraryStep(4, "Return past Commerce Square", 30, "Smooth ride back along water's edge.", "Central Lisbon")
            ),
            included = listOf("Private electric tuk-tuk", "Hot Pastéis de Belém pastry & bica coffee", "Guided commentary", "Hotel pickup"),
            excluded = listOf("Monastery museum entry", "Personal souvenirs"),
            availableTimeSlots = listOf("09:30 AM", "01:00 PM", "03:30 PM"),
            meetingPointAddress = "Praça do Comércio, 1100-148 Lisboa",
            pickupPoints = commonPickupPoints,
            availableExtras = commonExtras,
            assignedGuide = guides[0],
            reviews = emptyList()
        ),
        Tour(
            id = "tour_fatima_nazare",
            title = "Sacred Fátima Sanctuary & Nazaré Giant Waves Day Trip",
            tagline = "Spiritual Sanctuary of Fátima combined with Nazaré's world-record big wave ocean cliffs",
            destination = "Nazaré",
            category = TourCategory.DAY_TRIP,
            description = "Combine world-famous spiritual heritage and awe-inspiring natural ocean power on this full-day trip. Visit the Sanctuary of Our Lady of Fátima and Chapel of Apparitions, then head to Nazaré's Sítio cliff overlooking North Beach (Praia do Norte) where surfers ride 100-foot giant waves.",
            mainImageUrl = "https://images.unsplash.com/photo-1518684079-3c830dcef090?auto=format&fit=crop&w=1000&q=80",
            galleryImages = listOf(
                "https://images.unsplash.com/photo-1518684079-3c830dcef090?auto=format&fit=crop&w=1000&q=80"
            ),
            rating = 4.99,
            reviewCount = 114,
            durationHours = 7.0,
            basePriceEur = 280.0,
            perGuestPriceEur = 35.0,
            experienceType = ExperienceType.PRIVATE,
            languages = listOf("English", "Portuguese", "Spanish"),
            pickupAvailable = true,
            freeCancellationHours = 24,
            instantConfirmation = true,
            isFeatured = true,
            isPopular = false,
            isRecommended = true,
            isLastMinute = false,
            highlights = listOf(
                TourHighlight("Sanctuary of Fátima", "Basilica of Our Lady of the Rosary & Chapel of Apparitions."),
                TourHighlight("Nazaré Sítio Cliff Lookout", "High coastal headland with sweeping Atlantic views."),
                TourHighlight("Praia do Norte Big Wave Canyon", "Location of Garrett McNamara's world record 100ft wave ride."),
                TourHighlight("Traditional Nazaré Fisherman Village", "Fresh grilled sardines and dried fish traditions.")
            ),
            itinerary = listOf(
                ItineraryStep(1, "Lisbon Hotel Departure", 15, "Private comfortable journey towards Fátima.", "Hotel Pickup"),
                ItineraryStep(2, "Fátima Sanctuary Visit", 120, "Explore the Basilica, Chapel of Apparitions and quiet candle walk.", "Fátima Sanctuary"),
                ItineraryStep(3, "Nazaré Seafood Lunch & Sítio Cliff", 150, "Enjoy fresh seafood overlooking the immense Atlantic canyon.", "Nazaré Sítio"),
                ItineraryStep(4, "Praia do Norte & Fort of São Miguel Arcanjo", 90, "Visit the lighthouse museum dedicated to big wave surfing history.", "Nazaré Lighthouse"),
                ItineraryStep(5, "Return to Lisbon", 45, "Relax on return transport.", "Lisbon Hotel")
            ),
            included = listOf("Private tour transport", "Expert bilingual guide", "Fátima sanctuary tour", "Nazaré lighthouse museum entrance ticket", "Bottled water & snacks"),
            excluded = listOf("Lunch expenses", "Personal souvenirs"),
            availableTimeSlots = listOf("08:30 AM", "09:30 AM"),
            meetingPointAddress = "Lisbon Hotel Pickup / Central Departure",
            pickupPoints = commonPickupPoints,
            availableExtras = commonExtras,
            assignedGuide = guides[2],
            reviews = emptyList()
        ),
        Tour(
            id = "tour_fleet_convoy",
            title = "Private Electric Tuk-Tuk Convoy (Fleet for Groups & Weddings)",
            tagline = "Synchronized 2-to-5 vehicle eco-convoy with radio comms & group photos",
            destination = "Lisbon",
            category = TourCategory.FLEET_CONVOY,
            description = "The ultimate private group experience in Lisbon! Perfect for corporate retreats, destination weddings, family reunions, and celebrations. Travel in a synchronized fleet of 2 to 5 100% electric tuk-tuks traveling together across Lisbon's iconic hills. Guides stay linked via real-time two-way radios, and the convoy stops together at panoramic miradouros for unforgettable group photography.",
            mainImageUrl = "https://images.unsplash.com/photo-1548705085-101177834f47?auto=format&fit=crop&w=1000&q=80",
            galleryImages = listOf(
                "https://images.unsplash.com/photo-1548705085-101177834f47?auto=format&fit=crop&w=1000&q=80",
                "https://images.unsplash.com/photo-1588614959060-4d144f28b207?auto=format&fit=crop&w=1000&q=80",
                "https://images.unsplash.com/photo-1509824227185-9c5a01ceba0d?auto=format&fit=crop&w=1000&q=80"
            ),
            rating = 4.99,
            reviewCount = 118,
            durationHours = 2.5,
            basePriceEur = 220.0,
            perGuestPriceEur = 15.0,
            experienceType = ExperienceType.PRIVATE,
            languages = listOf("English", "Portuguese", "Spanish", "French", "German"),
            pickupAvailable = true,
            freeCancellationHours = 48,
            instantConfirmation = true,
            isFeatured = true,
            isPopular = true,
            isRecommended = true,
            isLastMinute = false,
            highlights = listOf(
                TourHighlight("Synchronized Fleet Convoy", "2 to 5 electric tuk-tuks driving together with synchronized team guidance."),
                TourHighlight("Radio Intercom Audio", "All vehicles equipped with synchronized audio and guide communications."),
                TourHighlight("Group Panorama Photography", "Dedicated stop at Miradouro da Graça with wide-angle professional group photos."),
                TourHighlight("Celebration Port Wine Toast", "Complimentary bottle of Port Wine & warm pastéis de nata for all guests.")
            ),
            itinerary = listOf(
                ItineraryStep(1, "Convoy Fleet Muster & Welcome", 20, "Meet your convoy lead guide and board your synchronized tuk-tuks.", "Praça do Comércio"),
                ItineraryStep(2, "Ascent to Graça & Panoramic Stop", 45, "Fleet convoy climbs the historic hills in formation to Graça viewpoint.", "Miradouro da Graça"),
                ItineraryStep(3, "Alfama Alleys & Sé Cathedral", 50, "Maneuver historic cobblestone quarters with coordinated stops.", "Alfama Quarter"),
                ItineraryStep(4, "Riverside Cruise & Toast", 35, "Celebration toast by the Tagus River.", "Cais das Colunas")
            ),
            included = listOf("Dedicated convoy fleet (2-5 electric tuk-tuks)", "Synchronized radio guides", "Group photography session", "Port wine toast & pastéis de nata", "Hotel pickup & drop-off"),
            excluded = listOf("Monuments entrance fees", "Gratuities"),
            availableTimeSlots = listOf("10:00 AM", "02:30 PM", "05:30 PM (Sunset Convoy)"),
            meetingPointAddress = "Praça do Comércio 12, 1100-148 Lisboa",
            pickupPoints = commonPickupPoints,
            availableExtras = commonExtras,
            assignedGuide = guides[0],
            reviews = listOf(
                TourReview("rev_convoy_1", "Charlotte & James", "United Kingdom", "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=200&q=80", 5, "2026-08-15", "Best part of our wedding weekend!", "We booked 3 tuk-tuks for our bridal party of 16. It was effortlessly coordinated, the drivers were incredible, and seeing our fleet roll through Lisbon was unforgettable!")
            )
        )
    )
}
