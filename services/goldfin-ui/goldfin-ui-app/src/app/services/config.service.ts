/*
 * Copyright (c) 2018 Goldfin.io. All Rights Reserved.
 */
import { Injectable } from '@angular/core';

export class Configuration {
  url_base: string;
}

const DEFAULT_CONFIG: Configuration = {
  url_base: "https://localhost:4000/api/v1" 
};

@Injectable()
export class ConfigurationService {
  getConfiguration(): Configuration {
    return DEFAULT_CONFIG;
  }
}
