package com.kipu.core.identity.domain.port.out;

import java.util.UUID;

public record ContactProfileInfo(UUID contactId, String firstName, String lastName) {}
