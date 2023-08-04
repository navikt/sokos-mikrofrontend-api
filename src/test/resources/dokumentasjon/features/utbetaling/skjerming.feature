# language: no
# encoding: UTF-8


Egenskap: Det er et personvernkrav at bare saksbehandlere med spesielle roller skal kunne se posteringer for personer
  som har adressebeskyttelse i PDL.

  Følgende nivåer av adressebeskyttelse finnes i PDL:
  * Fortolig
  * Strengt fortolig
  * Strengt fortrolig utland


  Scenario: Saksbehandler med vanlig leserolle skal kunne søke opp posteringer for personer som ikke er skjermet.

    Gitt en saksbehandler med følgende roller:
      | Rolle |
      | Les   |

    Og at følgende posteringer finnes i UR:
      | Rettighetshaver | Bilagsnummer | Posteringsbeløp | Mottaker    | Utbetalingsdato |
      | 24417337179     | 733740504    | -3572,00        | 24417337179 | 2022-12-12      |

    Og at følgende personer er skjermet:
      | Ident |

    Når posteringer søkes etter med følgende kriterier:
      | Rettighetshaver | Periodetype        | Periode FOM | Periode TOM |
      | 24417337179     | Utbetalingsperiode | 2022-12-01  | 2022-12-31  |

    Så skal følgende posteringer returneres:
      | Rettighetshaver | Bilagsnummer | Posteringsbeløp | Mottaker    | Utbetalingsdato |
      | 24417337179     | 733740504    | -3572,00        | 24417337179 | 2022-12-12      |


  Scenario: Saksbehandler med bare leserolle skal ikke kunne søke opp posteringer for personer som er skjermet.

    Gitt en saksbehandler med følgende roller:
      | Rolle |
      | Les   |

    Og at følgende posteringer finnes i UR:
      | Rettighetshaver | Bilagsnummer | Posteringsbeløp | Mottaker    | Utbetalingsdato |
      | 24417337179     | 733740504    | -3572,00        | 24417337179 | 2022-12-12      |

    Og at følgende personer er skjermet:
      | Ident       |
      | 24417337179 |

    Når posteringer søkes etter med følgende kriterier:
      | Rettighetshaver | Periodetype        | Periode FOM | Periode TOM |
      | 24417337179     | Utbetalingsperiode | 2022-12-01  | 2022-12-31  |

    Så skal følgende feilmelding gis: "Du har ikke tilgang til å søke opp denne personen."


  Scenario: Saksbehandler med rollen "0000-GA-okonomi-egne_ansatte" skal kunne søke opp posteringer for personer som er skjermet.

    Gitt en saksbehandler med følgende roller:
      | Rolle                        |
      | 0000-GA-okonomi-egne_ansatte |

    Og at følgende posteringer finnes i UR:
      | Rettighetshaver | Bilagsnummer | Posteringsbeløp | Mottaker    | Utbetalingsdato |
      | 24417337179     | 733740504    | -3572,00        | 24417337179 | 2022-12-12      |

    Og at følgende personer er skjermet:
      | Ident       |
      | 24417337179 |

    Når posteringer søkes etter med følgende kriterier:
      | Rettighetshaver | Periodetype        | Periode FOM | Periode TOM |
      | 24417337179     | Utbetalingsperiode | 2022-12-01  | 2022-12-31  |

    Så skal følgende posteringer returneres:
      | Rettighetshaver | Bilagsnummer | Posteringsbeløp | Mottaker    | Utbetalingsdato |
      | 24417337179     | 733740504    | -3572,00        | 24417337179 | 2022-12-12      |

